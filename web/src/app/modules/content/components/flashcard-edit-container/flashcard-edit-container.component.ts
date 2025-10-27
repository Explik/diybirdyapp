import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlashcardEditComponent } from "../flashcard-edit/flashcard-edit.component";
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { CdkDrag, CdkDragDrop, CdkDropList, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { EditFlashcard, EditFlashcardDeck, EditFlashcardDeckImpl, EditFlashcardImpl, EditFlashcardLanguageImpl } from '../../models/editFlashcard.model';
import { AudioInputComponent } from "../audio-input/audio-input.component";
import { ImageInputComponent } from "../image-input/image-input.component";
import { VideoInputComponent } from "../video-input/video-input.component";
import { LabelComponent } from "../../../../shared/components/label/label.component";
import { FormFieldComponent } from "../../../../shared/components/form-field/form-field.component";
import { SelectComponent } from "../../../../shared/components/select/select.component";
import { OptionComponent } from "../../../../shared/components/option/option.component";
import { ButtonComponent } from "../../../../shared/components/button/button.component";
import { TextInputComponent } from '../text-input/text-input.component';
import { FormErrorComponent } from "../../../../shared/components/form-error/form-error.component";

@Component({
  selector: 'app-flashcard-edit-container',
  standalone: true,
  templateUrl: './flashcard-edit-container.component.html',
  styleUrl: './flashcard-edit-container.component.css',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, DragDropModule, CdkDropList, CdkDrag, FlashcardEditComponent, TextFieldComponent, AudioInputComponent, ImageInputComponent, TextInputComponent, VideoInputComponent, LabelComponent, FormFieldComponent, SelectComponent, OptionComponent, ButtonComponent, FormErrorComponent]
})
export class FlashcardEditContainerComponent {
  @Input() flashcardDeck: EditFlashcardDeckImpl | undefined = undefined;
  @Input() flashcardLanguages: EditFlashcardLanguageImpl[] = [];

  @Output() saveFlashcards = new EventEmitter<void>();

  currentDragIndex: number | undefined = undefined;

  // Reactive form backing the template
  form: FormGroup = new FormGroup({
    flashcards: new FormArray([])
  });

  // Snapshot of original flashcard ids to detect adds/deletes on save
  private originalFlashcardIds: Set<string> | undefined = undefined;

  constructor(private fb: FormBuilder) { }

  ngOnChanges(): void {
    if (this.flashcardDeck) {
      // Initialize global language selectors with most common values
      // Do this before adding default flashcards to avoid undefined issues
      this.initializeGlobalLanguages();

      // Add default flashcards if none exist to avoid empty state
      if (!this.flashcardDeck.flashcards.length) {
        this.handleAddFlashcard();
      }

      // Build reactive form from the deck
      this.buildFormFromDeck();
    }
  }

  private initializeGlobalLanguages(): void {
    // Set the initial values for the global language selectors
    var frontLanguageId = this.getMostCommonLanguage('left') || '';
    var backLanguageId = this.getMostCommonLanguage('right') || '';

    this.form?.get('frontLanguageId')?.setValue(frontLanguageId);
    this.form?.get('backLanguageId')?.setValue(backLanguageId);
  }

  handleDragStart(index: number): void {
    this.currentDragIndex = index;
  }

  handleDragEnd(): void {
    this.currentDragIndex = undefined
  }

  handleRearrangeFlashcard(event: CdkDragDrop<Partial<EditFlashcardImpl>[]>): void {
    moveItemInArray(this.flashcardDeck!.flashcards, event.previousIndex, event.currentIndex);
    this.flashcardDeck!.flashcards
      .filter(s => s.state !== 'deleted')
      .forEach((flashcard, index) => flashcard.deckOrder = index + 1);
  }

  handleAddFlashcard() {
    var newFlashcard = EditFlashcardImpl.createDefault();
    // Assign a unique id for change detection (temporary if needed)
    newFlashcard.state = 'added';
    newFlashcard.id = this.generateUniqueId();
    newFlashcard.deckId = this.flashcardDeck!.id;
    newFlashcard.deckOrder = this.flashcardDeck!.flashcards.length + 1;

    // Push into the UI model (but don't mark as 'added' yet) — detection happens on save
    this.flashcardDeck!.flashcards.push(newFlashcard);
    // Also add to the reactive form array if available
    if (this.form) {
      const fa = this.getFlashcardsFormArray();
      const fg = this.buildFlashcardFormGroup(newFlashcard);
      // attach per-flashcard validator
      fg.setValidators(this.flashcardValidator());
      fa.push(fg);
    }
  }

  private generateUniqueId(): string {
    return `tmp-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
  }

  private getMostCommonLanguage(side: 'left' | 'right'): string | undefined {
    const languageCounts = new Map<string, number>();

    for (const flashcard of this.flashcardDeck!.flashcards) {
      if (flashcard.state === 'deleted') continue;

      let languageId: string | undefined;
      if (side === 'left' && flashcard.leftContentType === 'text' && flashcard.leftTextContent) {
        languageId = flashcard.leftTextContent.languageId;
      } else if (side === 'right' && flashcard.rightContentType === 'text' && flashcard.rightTextContent) {
        languageId = flashcard.rightTextContent.languageId;
      }

      if (languageId && languageId !== '') {
        languageCounts.set(languageId, (languageCounts.get(languageId) || 0) + 1);
      }
    }

    // Return the most common language, or undefined if no languages found
    if (languageCounts.size === 0) return undefined;

    return Array.from(languageCounts.entries())
      .reduce((a, b) => a[1] > b[1] ? a : b)[0];
  }

  handleDeleteFlashcard(flashcard: EditFlashcardImpl): void {
    // Remove the flashcard from the form array and the UI model immediately
    // Find index in the deck
    if (!this.flashcardDeck) return;

    const idx = this.flashcardDeck.flashcards.findIndex(f => f === flashcard || f.id === flashcard.id);
    if (idx !== -1) {
      // Remove from underlying model
      this.flashcardDeck.flashcards.splice(idx, 1);

      // Remove from reactive form array if present
      if (this.form) {
        const fa = this.getFlashcardsFormArray();
        if (fa && fa.length > idx) {
          fa.removeAt(idx);
        }
      }

      // Recompute deckOrder for remaining items
      this.flashcardDeck.flashcards
        .forEach((f, index) => f.deckOrder = index + 1);
    }
  }


  // --- Reactive form helpers ---
  private buildFormFromDeck(): void {
    if (!this.flashcardDeck) return;

    this.form = this.fb.group({
      name: [this.flashcardDeck.name, [Validators.required]],
      description: [this.flashcardDeck.description],
      frontLanguageId: [this.getMostCommonLanguage('left')],
      backLanguageId: [this.getMostCommonLanguage('right')],
      flashcards: this.fb.array(this.flashcardDeck.flashcards.map(f => {
        const fg = this.buildFlashcardFormGroup(f);
        fg.setValidators(this.flashcardValidator());
        return fg;
      }))
    });

    // attach deck-level validator (e.g., require language selects when text exists)
    this.form.setValidators(this.deckValidator());

    // Snapshot original ids for add/delete detection on save
    this.originalFlashcardIds = new Set(this.flashcardDeck.flashcards.map(f => f.id));

    // Ensure validators run at least once after building
    this.form.updateValueAndValidity({ onlySelf: false, emitEvent: false });
  }

  private buildFlashcardFormGroup(f: EditFlashcardImpl) {
    return this.fb.group({
      id: [f.id],
      deckId: [f.deckId],
      deckOrder: [f.deckOrder],
      // TODO correctly initialize content types and contents
      leftContentType: [f.leftContentType],
      rightContentType: [f.rightContentType],
      leftTextContent: [f.leftTextContent],
      rightTextContent: [f.rightTextContent],
      leftAudioContent: [f.leftAudioContent],
      rightAudioContent: [f.rightAudioContent],
      leftImageContent: [f.leftImageContent],
      rightImageContent: [f.rightImageContent],
      leftVideoContent: [f.leftVideoContent],
      rightVideoContent: [f.rightVideoContent]
    });
  }

  private getFlashcardsFormArray(): FormArray {
    return this.form!.get('flashcards') as FormArray;
  }

  // Template-friendly accessor for the form array controls
  get flashcardsControls() {
    return (this.form?.get('flashcards') as FormArray)?.controls || [];
  }

  /**
   * Return the language name for a given language id using the provided
   * flashcardLanguages input. If no match is found, return the id as a
   * fallback so the UI still shows something.
   */
  getLanguageName(id?: string | null): string | undefined {
    if (!id) return undefined;
    const lang = this.flashcardLanguages?.find(l => l.id === id);
    return lang ? lang.name : id;
  }

  handleSaveFlashcards() {
    // Trigger validators and mark touched so template shows errors. Abort save if invalid.
    if (this.form) {
      this.form.markAllAsTouched();
      this.form.updateValueAndValidity({ onlySelf: false, emitEvent: true });
      if (!this.form.valid) return;
    }

    // Before saving, apply the globally selected languages to individual flashcards
    const front = this.form?.get('frontLanguageId')?.value;
    const back = this.form?.get('backLanguageId')?.value;

    if (this.flashcardDeck?.flashcards?.length) {
      for (let flashcard of this.flashcardDeck.flashcards) {
        if (flashcard.state === 'deleted') continue;
        if (front && flashcard.leftContentType === 'text' && flashcard.leftTextContent) {
          flashcard.leftTextContent.languageId = front;
        }
        if (back && flashcard.rightContentType === 'text' && flashcard.rightTextContent) {
          flashcard.rightTextContent.languageId = back;
        }
      }
    }

    // Detect added / deleted flashcards by comparing original IDs to current (ignoring deleted marked ones)
    const currentActiveIds = (this.flashcardDeck?.flashcards || [])
      .filter(f => f.state !== 'deleted')
      .map(f => f.id);

    const originalIds = this.originalFlashcardIds ? Array.from(this.originalFlashcardIds) : [];

    const addedIds = currentActiveIds.filter(id => !originalIds.includes(id));
    const deletedIds = originalIds.filter(id => !currentActiveIds.includes(id));

    // Mark added ones with state='added' so downstream code can pick them up
    if (addedIds.length) {
      for (let id of addedIds) {
        const f = this.flashcardDeck!.flashcards.find(x => x.id === id);
        if (f) f.state = 'added';
      }
    }

    // For any original items that are now deleted, ensure state='deleted'
    if (deletedIds.length) {
      for (let id of deletedIds) {
        const f = this.flashcardDeck!.flashcards.find(x => x.id === id);
        if (f) f.state = 'deleted';
      }
    }

    this.saveFlashcards?.emit();
  }

  /**
   * Validator for an individual flashcard FormGroup. Ensures the selected content type
   * has corresponding content present for left/right sides. Sets errors on child controls
   * so the template can show field-level messages.
   */
  private flashcardValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control) return null;

      let hasError = false;

      const leftType = control.get('leftContentType')?.value;
      const rightType = control.get('rightContentType')?.value;

      const leftText = control.get('leftTextContent');
      const rightText = control.get('rightTextContent');
      const leftImage = control.get('leftImageContent');
      const rightImage = control.get('rightImageContent');
      const leftAudio = control.get('leftAudioContent');
      const rightAudio = control.get('rightAudioContent');
      const leftVideo = control.get('leftVideoContent');
      const rightVideo = control.get('rightVideoContent');

      // clear prior child errors
      [leftText, rightText, leftImage, rightImage, leftAudio, rightAudio, leftVideo, rightVideo].forEach(c => {
        if (c) c.setErrors(null);
      });

      if (leftType === 'text') {
        const val = leftText?.value?.text;
        if (!val || (typeof val === 'string' && val.trim() === '')) {
          leftText?.setErrors({ 'text.required': true });
          hasError = true;
        }
      } else if (leftType === 'image') {
        const v = leftImage?.value;
        if (!v || (!(v.imageFile) && !(v.imageUrl))) {
          leftImage?.setErrors({ 'image.required': true });
          hasError = true;
        }
      } else if (leftType === 'audio') {
        const v = leftAudio?.value;
        if (!v) { leftAudio?.setErrors({ 'audio.required': true }); hasError = true; }
      } else if (leftType === 'video') {
        const v = leftVideo?.value;
        if (!v || (!(v.videoFile) && !(v.videoUrl))) { leftVideo?.setErrors({ 'video.required': true }); hasError = true; }
      }

      if (rightType === 'text') {
        const val = rightText?.value?.text;
        if (!val || (typeof val === 'string' && val.trim() === '')) {
          rightText?.setErrors({ 'text.required': true });
          hasError = true;
        }
      } else if (rightType === 'image') {
        const v = rightImage?.value;
        if (!v || (!(v.imageFile) && !(v.imageUrl))) {
          rightImage?.setErrors({ 'image.required': true });
          hasError = true;
        }
      } else if (rightType === 'audio') {
        const v = rightAudio?.value;
        if (!v) { rightAudio?.setErrors({ 'audio.required': true }); hasError = true; }
      } else if (rightType === 'video') {
        const v = rightVideo?.value;
        if (!v || (!(v.videoFile) && !(v.videoUrl))) { rightVideo?.setErrors({ 'video.required': true }); hasError = true; }
      }

      return hasError ? { flashcardInvalid: true } : null;
    }
  }

  /**
   * Deck-level validator: if any flashcard has text content on either side, require the
   * corresponding global language selector to be set.
   */
  private deckValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control) return null;

      const frontCtrl = control.get('frontLanguageId');
      const backCtrl = control.get('backLanguageId');

      if (frontCtrl) frontCtrl.setErrors(null);
      if (backCtrl) backCtrl.setErrors(null);

      const fa = control.get('flashcards') as FormArray | null;
      let anyLeftText = false;
      let anyRightText = false;

      if (fa) {
        for (let i = 0; i < fa.length; i++) {
          const fg = fa.at(i);
          const leftType = fg.get('leftContentType')?.value;
          const rightType = fg.get('rightContentType')?.value;

          if (leftType === 'text') {
            const val = fg.get('leftTextContent')?.value?.text;
            if (val && typeof val === 'string' && val.trim() !== '') anyLeftText = true;
          }
          if (rightType === 'text') {
            const val = fg.get('rightTextContent')?.value?.text;
            if (val && typeof val === 'string' && val.trim() !== '') anyRightText = true;
          }
        }
      }

      let hasError = false;
      if (anyLeftText) {
        const front = frontCtrl?.value;
        if (!front || front === '') { frontCtrl?.setErrors({ 'language.required': true }); hasError = true; }
      }
      if (anyRightText) {
        const back = backCtrl?.value;
        if (!back || back === '') { backCtrl?.setErrors({ 'language.required': true }); hasError = true; }
      }

      return hasError ? { languagesMissing: true } : null;
    }
  }
}

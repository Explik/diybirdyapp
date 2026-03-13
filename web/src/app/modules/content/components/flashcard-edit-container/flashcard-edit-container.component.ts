import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlashcardEditComponent } from "../flashcard-edit/flashcard-edit.component";
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { CdkDrag, CdkDragDrop, CdkDropList, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { EditFlashcard, EditFlashcardDeck, EditFlashcardDeckImpl, EditFlashcardImpl, EditFlashcardLanguageImpl } from '../../models/editFlashcard.model';
import { LabelComponent } from "../../../../shared/components/label/label.component";
import { FormFieldComponent } from "../../../../shared/components/form-field/form-field.component";
import { SelectComponent } from "../../../../shared/components/select/select.component";
import { OptionComponent } from "../../../../shared/components/option/option.component";
import { ButtonComponent } from "../../../../shared/components/button/button.component";
import { FormErrorComponent } from "../../../../shared/components/form-error/form-error.component";
import { FlashcardLanguageDto } from '../../../../shared/api-client';
import { LOCALE_ID } from '@angular/core';

@Component({
  selector: 'app-flashcard-edit-container',
  standalone: true,
  templateUrl: './flashcard-edit-container.component.html',
  styleUrl: './flashcard-edit-container.component.css',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, DragDropModule, CdkDropList, CdkDrag, FlashcardEditComponent, TextFieldComponent, LabelComponent, FormFieldComponent, SelectComponent, OptionComponent, ButtonComponent, FormErrorComponent]
})
export class FlashcardEditContainerComponent {
  @Input() flashcardDeck: EditFlashcardDeckImpl | undefined = undefined;
  @Input() flashcardLanguages: EditFlashcardLanguageImpl[] = [];

  @Output() saveFlashcards = new EventEmitter<void>();

  currentDragIndex: number | undefined = undefined;
  displayNames: Intl.DisplayNames;

  // Reactive form backing the template
  form: FormGroup = new FormGroup({
    flashcards: new FormArray([])
  });

  // Snapshot of original flashcard ids to detect adds/deletes on save
  private originalFlashcardIds: Set<string> | undefined = undefined;

  constructor(private fb: FormBuilder, @Inject(LOCALE_ID) private locale: string) {
    this.displayNames = new Intl.DisplayNames([this.locale], { type: 'language' });
  }

  ngOnChanges(): void {
    if (this.flashcardDeck) {
      // Initialize global language selectors with most common values
      // Do this before adding default flashcards to avoid undefined issues
      this.initializeGlobalLanguages();

      // Add default flashcards if none exist to avoid empty state
      if (!this.flashcardDeck.flashcards.length) {
        this.handleAddFlashcard();
      }

      // Build reactive form from the deck (excluding deleted items)
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
    // Get active (non-deleted) flashcards and their indices in the full array
    const activeFlashcards = this.flashcardDeck!.flashcards
      .map((f, index) => ({ flashcard: f, originalIndex: index }))
      .filter(item => item.flashcard.state !== 'deleted');

    // Rearrange the active flashcards
    const movedItem = activeFlashcards[event.previousIndex];
    activeFlashcards.splice(event.previousIndex, 1);
    activeFlashcards.splice(event.currentIndex, 0, movedItem);

    // Update deckOrder for all active flashcards
    activeFlashcards.forEach((item, index) => {
      item.flashcard.deckOrder = index + 1;
    });
    
    // Update the FormArray to reflect the new order in the UI
    const formArray = this.getFlashcardsFormArray();
    const movedControl = formArray.at(event.previousIndex);
    formArray.removeAt(event.previousIndex);
    formArray.insert(event.currentIndex, movedControl);
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

  public formatLanguageName(language: FlashcardLanguageDto) {
    if (language.isoCode) {
      return this.displayNames.of(language.isoCode) || language.name || 'N/A';
    }
    return language.name || 'N/A';
  }

  public getFrontLanguageName(): string {
    const frontId = this.form?.get('frontLanguageId')?.value;
    const lang = this.flashcardLanguages.find(l => l.id === frontId);
    return lang ? this.formatLanguageName(lang) : 'N/A';
  }

  public getBackLanguageName(): string {
    const backId = this.form?.get('backLanguageId')?.value;
    const lang = this.flashcardLanguages.find(l => l.id === backId);
    return lang ? this.formatLanguageName(lang) : 'N/A';
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
    // Mark flashcard as deleted instead of removing it
    // This keeps it in the change tracker until save
    if (!this.flashcardDeck) return;

    const idx = this.flashcardDeck.flashcards.findIndex(f => f === flashcard || f.id === flashcard.id);
    if (idx !== -1) {
      // Mark as deleted in the underlying model
      this.flashcardDeck.flashcards[idx].state = 'deleted';

      // Find the corresponding index in the form array (which only has non-deleted items)
      if (this.form) {
        const fa = this.getFlashcardsFormArray();
        const formIndex = this.flashcardDeck.flashcards
          .slice(0, idx)
          .filter(f => f.state !== 'deleted')
          .length;
        
        if (fa && fa.length > formIndex) {
          fa.removeAt(formIndex);
        }
      }

      // Recompute deckOrder for remaining active items
      this.flashcardDeck.flashcards
        .filter(f => f.state !== 'deleted')
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
      flashcards: this.fb.array(this.flashcardDeck.flashcards
        .filter(f => f.state !== 'deleted')
        .map(f => {
          const fg = this.buildFlashcardFormGroup(f);
          fg.setValidators(this.flashcardValidator());
          return fg;
        }))
    });

    // attach deck-level validator (e.g., require language selects when text exists)
    this.form.setValidators(this.deckValidator());

    // Snapshot original ids for add/delete detection on save (only non-deleted items)
    this.originalFlashcardIds = new Set(this.flashcardDeck.flashcards
      .filter(f => f.state !== 'deleted')
      .map(f => f.id));

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
  // Note: Deleted items are already removed from the form array in handleDeleteFlashcard
  get flashcardsControls(): FormGroup[] {
    return ((this.form?.get('flashcards') as FormArray)?.controls as FormGroup[]) || [];
  }

  handleDeleteFlashcardAt(index: number): void {
    if (!this.flashcardDeck) return;
    
    // Map form array index to flashcard (form array only contains non-deleted items)
    const activeFlashcards = this.flashcardDeck.flashcards.filter(f => f.state !== 'deleted');
    const flashcard = activeFlashcards[index];
    
    if (flashcard) {
      this.handleDeleteFlashcard(flashcard);
    }
  }

  private syncFlashcardModelsFromForm(): void {
    if (!this.flashcardDeck || !this.form) return;

    const formGroupsById = new Map<string, FormGroup>();
    const flashcardFormGroups = this.getFlashcardsFormArray().controls as FormGroup[];

    for (const fg of flashcardFormGroups) {
      const id = fg.get('id')?.value;
      if (id) {
        formGroupsById.set(id, fg);
      }
    }

    for (const flashcard of this.flashcardDeck.flashcards) {
      if (flashcard.state === 'deleted') continue;

      const fg = formGroupsById.get(flashcard.id);
      if (!fg) continue;

      const nextLeftType = fg.get('leftContentType')?.value ?? flashcard.leftContentType;
      const nextRightType = fg.get('rightContentType')?.value ?? flashcard.rightContentType;
      const contentTypeChanged =
        flashcard.leftContentType !== nextLeftType ||
        flashcard.rightContentType !== nextRightType;

      flashcard.leftContentType = nextLeftType;
      flashcard.rightContentType = nextRightType;

      flashcard.leftTextContent = fg.get('leftTextContent')?.value;
      flashcard.rightTextContent = fg.get('rightTextContent')?.value;
      flashcard.leftAudioContent = fg.get('leftAudioContent')?.value;
      flashcard.rightAudioContent = fg.get('rightAudioContent')?.value;
      flashcard.leftImageContent = fg.get('leftImageContent')?.value;
      flashcard.rightImageContent = fg.get('rightImageContent')?.value;
      flashcard.leftVideoContent = fg.get('leftVideoContent')?.value;
      flashcard.rightVideoContent = fg.get('rightVideoContent')?.value;

      if (contentTypeChanged && flashcard.state === 'unchanged') {
        flashcard.state = 'updated';
      }
    }
  }

  handleSaveFlashcards() {
    // Trigger validators and mark touched so template shows errors. Abort save if invalid.
    if (this.form) {
      this.form.markAllAsTouched();
      this.form.updateValueAndValidity({ onlySelf: false, emitEvent: true });
      if (!this.form.valid) return;
    }

    // Apply flashcard deck changes to the model
    if (this.form) {
      this.flashcardDeck!.name = this.form.get('name')?.value;
      this.flashcardDeck!.description = this.form.get('description')?.value;
    }

    // Form controls are the source of truth while editing; persist them into the model before save.
    this.syncFlashcardModelsFromForm();

    // Before saving, apply the globally selected languages to individual flashcards
    const front = this.form?.get('frontLanguageId')?.value;
    const back = this.form?.get('backLanguageId')?.value;

    for (let flashcard of this.flashcardDeck!.flashcards) {
      if (flashcard.state === 'deleted') continue;
      if (front && flashcard.leftContentType === 'text' && flashcard.leftTextContent) {
        flashcard.leftTextContent.languageId = front;
      }
      if (back && flashcard.rightContentType === 'text' && flashcard.rightTextContent) {
        flashcard.rightTextContent.languageId = back;
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

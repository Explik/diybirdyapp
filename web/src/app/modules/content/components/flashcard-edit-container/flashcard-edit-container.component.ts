import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule } from '@angular/common';
import { FlashcardEditComponent } from "../flashcard-edit/flashcard-edit.component";
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { FormsModule } from '@angular/forms';
import { CdkDrag, CdkDragDrop, CdkDropList, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { EditFlashcard, EditFlashcardImpl, EditFlashcardLanguageImpl } from '../../models/editFlashcard.model';
import { AudioInputComponent } from "../audio-input/audio-input.component";
import { ImageInputComponent } from "../image-input/image-input.component";
import { VideoInputComponent } from "../video-input/video-input.component";
import { TextInputComponent } from "../text-input/text-input.component";

@Component({
    selector: 'app-flashcard-edit-container',
    standalone: true,
    templateUrl: './flashcard-edit-container.component.html',
    styleUrl: './flashcard-edit-container.component.css',
    imports: [TextButtonComponent, CommonModule, FormsModule, DragDropModule, CdkDropList, CdkDrag, FlashcardEditComponent, TextFieldComponent, AudioInputComponent, ImageInputComponent, VideoInputComponent, TextInputComponent]
})
export class FlashcardEditContainerComponent {
  _name: string | undefined = undefined;

  @Input()
  get name() {
    return this._name;
  } 
  set name(val: string | undefined) {
    this.nameChange.emit(val);
    this._name = val;
  }
  @Output() nameChange = new EventEmitter<string>();

  @Input() flashcards: EditFlashcardImpl[] = [];
  @Input() flashcardLanguages: EditFlashcardLanguageImpl[] = [];
  
  @Output() saveFlashcards = new EventEmitter<void>();

  currentDragIndex: number | undefined = undefined;

  handleDragStart(index: number): void {
    this.currentDragIndex = index;
  }

  handleDragEnd(): void {
    this.currentDragIndex = undefined
  }

  handleRearrangeFlashcard(event: CdkDragDrop<Partial<EditFlashcardImpl>[]>): void {
    moveItemInArray(this.flashcards, event.previousIndex, event.currentIndex);
    this.flashcards
      .filter(s => s.state !== 'deleted')
      .forEach((flashcard, index) => flashcard.deckOrder = index + 1);
  }

  handleAddFlashcard() {
    var newFlashcard = EditFlashcardImpl.createDefault();
    newFlashcard.state = 'added';
    newFlashcard.deckId =  this.flashcards[0].deckId;
    newFlashcard.deckOrder = this.flashcards.length + 1;
    newFlashcard.leftTextContent!.languageId = this.flashcardLanguages[0].id;
    newFlashcard.rightTextContent!.languageId = this.flashcardLanguages[1].id;
    this.flashcards.push(newFlashcard);
  }

  handleDeleteFlashcard(flashcard: EditFlashcardImpl): void {
    if (flashcard.state === 'added') {
      this.flashcards = this.flashcards.filter(s => s !== flashcard);
    }
    else flashcard.state = 'deleted';

    this.flashcards
      .filter(s => s.state !== 'deleted')
      .forEach((flashcard, index) => flashcard.deckOrder = index + 1);
  }

  handleUpdateLeftLanguage(event: Event): void {
    const selectedLanguageId = (event.target as HTMLSelectElement).value;
    const selectedLanguage = this.flashcardLanguages.find(l => l.id == selectedLanguageId);
    if (!selectedLanguage) 
      return;

    for(let flashcard of this.flashcards) {
      //flashcard.leftLanguage = selectedLanguage;
    }
  }

  handleUpdateRightLanguage(event: Event): void {
    const selectedLanguageId = (event.target as HTMLSelectElement).value;
    const selectedLanguage = this.flashcardLanguages.find(l => l.id == selectedLanguageId);
    if (!selectedLanguage) 
      return;

    for(let flashcard of this.flashcards) {
      //flashcard.rightLanguage = selectedLanguage;
    }
  }

  handleSaveFlashcards() {
    this.saveFlashcards?.emit();
  }
}

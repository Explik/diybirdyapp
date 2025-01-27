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

@Component({
    selector: 'app-flashcard-edit-container',
    standalone: true,
    templateUrl: './flashcard-edit-container.component.html',
    styleUrl: './flashcard-edit-container.component.css',
    imports: [TextButtonComponent, CommonModule, FormsModule, DragDropModule, CdkDropList, CdkDrag, FlashcardEditComponent, TextFieldComponent, AudioInputComponent, ImageInputComponent, VideoInputComponent]
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
  
  @Output() addFlashcard = new EventEmitter<void>();
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
    // TODO persist change of order
  }

  handleAddFlashcard() {
    this.addFlashcard?.emit();
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

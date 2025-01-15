import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FlashcardLanguage, Flashcard } from '../../models/flashcard.model';
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule } from '@angular/common';
import { FlashcardEditComponent } from "../flashcard-edit/flashcard-edit.component";
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-flashcard-edit-container',
    standalone: true,
    templateUrl: './flashcard-edit-container.component.html',
    styleUrl: './flashcard-edit-container.component.css',
    imports: [TextButtonComponent, CommonModule, FormsModule, FlashcardEditComponent, TextFieldComponent]
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

  @Input() flashcards: Partial<Flashcard>[] = [];
  @Input() flashcardLanguages: FlashcardLanguage[] = [];
  
  @Output() addFlashcard = new EventEmitter<void>();
  @Output() saveFlashcards = new EventEmitter<void>();

  handleAddFlashcard() {
    this.addFlashcard?.emit();
  }

  handleUpdateLeftLanguage(event: Event): void {
    const selectedLanguageId = (event.target as HTMLSelectElement).value;
    const selectedLanguage = this.flashcardLanguages.find(l => l.id == selectedLanguageId);
    if (!selectedLanguage) 
      return;

    for(let flashcard of this.flashcards) {
      flashcard.leftLanguage = selectedLanguage;
    }
  }

  handleUpdateRightLanguage(event: Event): void {
    const selectedLanguageId = (event.target as HTMLSelectElement).value;
    const selectedLanguage = this.flashcardLanguages.find(l => l.id == selectedLanguageId);
    if (!selectedLanguage) 
      return;

    for(let flashcard of this.flashcards) {
      flashcard.rightLanguage = selectedLanguage;
    }
  }

  handleSwitchFlashcardSides(flashcard: Partial<Flashcard>): void {
    var temp = flashcard.leftValue;
    flashcard.leftValue = flashcard.rightValue;
    flashcard.rightValue = temp;
  }

  handleSaveFlashcards() {
    this.saveFlashcards?.emit();
  }
}

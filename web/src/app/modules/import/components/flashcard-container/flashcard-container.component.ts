import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FlashcardLanguage, Flashcard } from '../../models/flashcard.model';
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule } from '@angular/common';
import { GenericFlashcardComponent } from "../generic-flashcard/generic-flashcard.component";
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-flashcard-container',
    standalone: true,
    templateUrl: './flashcard-container.component.html',
    styleUrl: './flashcard-container.component.css',
    imports: [TextButtonComponent, CommonModule, FormsModule, GenericFlashcardComponent, TextFieldComponent]
})
export class FlashcardContainerComponent {
  @Input() flashcards: Flashcard[] = [];
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

  handleSwitchFlashcardSides(flashcard: Flashcard): void {
    var temp = flashcard.leftValue;
    flashcard.leftValue = flashcard.rightValue;
    flashcard.rightValue = temp;
  }

  handleSaveFlashcards() {
    this.saveFlashcards?.emit();
  }
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FlashcardLanguage, TranslationFlashcard } from '../../models/flashcard.model';
import { TranslationFlashcardComponent } from "../translation-flashcard/translation-flashcard.component";
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-flashcard-container',
    standalone: true,
    templateUrl: './flashcard-container.component.html',
    styleUrl: './flashcard-container.component.css',
    imports: [TranslationFlashcardComponent, TextButtonComponent, CommonModule]
})
export class FlashcardContainerComponent {
  @Input() flashcards: TranslationFlashcard[] = [];
  @Input() flashcardLanguages: FlashcardLanguage[] = [];

  @Output() updateLeftLanguage = new EventEmitter<string>();
  @Output() updateRightLanguage = new EventEmitter<string>();
  @Output() addFlashcard = new EventEmitter<void>();
  @Output() saveFlashcards = new EventEmitter<void>();

  handleAddFlashcard() {
    this.addFlashcard?.emit();
  }

  handleUpdateLeftLanguage(event: Event): void {
    const selectedId = (event.target as HTMLSelectElement).value;
    this.updateLeftLanguage?.emit(selectedId);
  }

  handleUpdateRightLanguage(event: Event): void {
    const selectedId = (event.target as HTMLSelectElement).value;
    this.updateRightLanguage?.emit(selectedId);
  }

  handleSaveFlashcards() {
    this.saveFlashcards?.emit();
  }
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslationFlashcard } from '../../models/flashcard.model';
import { TranslationFlashcardComponent } from "../translation-flashcard/translation-flashcard.component";
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";

@Component({
    selector: 'app-flashcard-container',
    standalone: true,
    templateUrl: './flashcard-container.component.html',
    styleUrl: './flashcard-container.component.css',
    imports: [TranslationFlashcardComponent, TextButtonComponent]
})
export class FlashcardContainerComponent {
  @Input() flashcards: TranslationFlashcard[] = [];
  @Output() addFlashcard = new EventEmitter<void>();
  @Output() saveFlashcards = new EventEmitter<void>();

  handleAddFlashcard() {
    this.addFlashcard?.emit();
  }

  handleSaveFlashcards() {
    this.saveFlashcards?.emit();
  }
}

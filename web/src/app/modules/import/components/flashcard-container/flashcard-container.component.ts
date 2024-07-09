import { Component, Input } from '@angular/core';
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

  handleAddFlashcard() {
    const leftLabel = this.flashcards.length ? this.flashcards[0].leftLabel : "";
    const rightLabel = this.flashcards.length ? this.flashcards[0].rightLabel : "";

    this.flashcards.push({ leftLabel, rightLabel, leftValue: "", rightValue: "" });
  }
}

import { Component, Input } from '@angular/core';
import { FlashcardReviewComponent } from '../flashcard-review/flashcard-review.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-flashcard-review-container',
  templateUrl: './flashcard-review-container.component.html',
  styleUrls: ['./flashcard-review-container.component.css'],
  standalone: true,
  imports: [CommonModule, FlashcardReviewComponent]
})
export class FlashcardReviewContainerComponent {
  @Input() flashcards: any[] = [];
  currentIndex: number = 0;

  previousFlashcard() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  nextFlashcard() {
    if (this.currentIndex < this.flashcards.length - 1) {
      this.currentIndex++;
    }
  }
}

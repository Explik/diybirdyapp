import { Component } from '@angular/core';

@Component({
  selector: 'app-flashcard-review',
  standalone: true,
  imports: [],
  templateUrl: './flashcard-review.component.html',
  styleUrl: './flashcard-review.component.css'
})
export class FlashcardReviewComponent {
  isFlipped = false;

  flipCard() {
    this.isFlipped = !this.isFlipped;
  }
}

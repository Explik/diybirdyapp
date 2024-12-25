import { Input, Component } from '@angular/core';

@Component({
  selector: 'app-flashcard-review',
  standalone: true,
  imports: [],
  templateUrl: './flashcard-review.component.html',
  styleUrl: './flashcard-review.component.css'
})
export class FlashcardReviewComponent {
  @Input() flippable = true;
  isFlipped = false;

  flipCard() {
    if (this.flippable) {
      this.isFlipped = !this.isFlipped;
    }
  }
}

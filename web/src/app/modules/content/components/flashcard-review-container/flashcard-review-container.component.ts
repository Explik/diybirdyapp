import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { Flashcard } from '../../models/flashcard.model';
import { AudioPreviewComponent } from "../audio-preview/audio-preview.component";
import { ImagePreviewComponent } from "../image-preview/image-preview.component";

@Component({
  selector: 'app-flashcard-review-container',
  templateUrl: './flashcard-review-container.component.html',
  styleUrls: ['./flashcard-review-container.component.css'],
  standalone: true,
  imports: [CommonModule, FlashcardComponent, FlashcardComponent, AudioPreviewComponent, ImagePreviewComponent]
})
export class FlashcardReviewContainerComponent {
  @Input() flashcards: any[] = [];
  currentIndex: number = 0;
  
  get currentFlashcard() {
    return this.flashcards.length > 0 ? this.flashcards[this.currentIndex] : undefined;
  }

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

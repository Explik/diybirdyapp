import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { AudioPreviewComponent } from "../audio-preview/audio-preview.component";
import { ImagePreviewComponent } from "../image-preview/image-preview.component";
import { VideoPreviewComponent } from "../video-preview/video-preview.component";

@Component({
  selector: 'app-flashcard-review-container',
  templateUrl: './flashcard-review-container.component.html',
  styleUrls: ['./flashcard-review-container.component.css'],
  standalone: true,
  imports: [CommonModule, FlashcardComponent, FlashcardComponent, AudioPreviewComponent, ImagePreviewComponent, VideoPreviewComponent]
})
export class FlashcardReviewContainerComponent {
  @Input() flashcards: any[] = [];
  currentIndex: number = 0;
  
  get currentFlashcard() {
    var nonDeletedFlashcards = this.flashcards.filter(flashcard => flashcard.state !== 'deleted');
    return nonDeletedFlashcards.length > 0 ? nonDeletedFlashcards[this.currentIndex] : undefined;
  }

  previousFlashcard() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  nextFlashcard() {
    var nonDeletedFlashcards = this.flashcards.filter(flashcard => flashcard.state !== 'deleted');
    if (this.currentIndex < nonDeletedFlashcards.length - 1) {
      this.currentIndex++;
    }
  }
}

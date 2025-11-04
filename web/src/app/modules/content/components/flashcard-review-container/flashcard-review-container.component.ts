import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { AudioPreviewComponent } from "../audio-preview/audio-preview.component";
import { ImagePreviewComponent } from "../image-preview/image-preview.component";
import { VideoPreviewComponent } from "../video-preview/video-preview.component";
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-flashcard-review-container',
  templateUrl: './flashcard-review-container.component.html',
  styleUrls: ['./flashcard-review-container.component.css'],
  standalone: true,
  imports: [CommonModule, FlashcardComponent, FlashcardComponent, AudioPreviewComponent, ImagePreviewComponent, VideoPreviewComponent, IconComponent, ButtonComponent]
})
export class FlashcardReviewContainerComponent {
  @Input() flashcards: any[] = [];
  currentIndex: number = 0;
  
  constructor(private service: AudioPlayingService) {}

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

  playAudio(side: 'left' | 'right') {

    console.log(`Request to play audio for ${side} side`);
    
    if (!this.currentFlashcard) return;

    this.service.startPlayingReviewFlashcard(this.currentFlashcard.id, side);
    console.log(`Playing audio for ${side} side of flashcard ID ${this.currentFlashcard.id}`);
    
  }
}

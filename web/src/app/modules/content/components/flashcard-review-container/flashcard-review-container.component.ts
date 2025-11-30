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
  currentSide: 'front' | 'back' = 'front';
  
  constructor(private service: AudioPlayingService) {}

  get currentFlashcard() {
    var nonDeletedFlashcards = this.flashcards.filter(flashcard => flashcard.state !== 'deleted');
    return nonDeletedFlashcards.length > 0 ? nonDeletedFlashcards[this.currentIndex] : undefined;
  }

  previousFlashcard() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
      this.currentSide = 'front';
    }
  }

  nextFlashcard() {
    var nonDeletedFlashcards = this.flashcards.filter(flashcard => flashcard.state !== 'deleted');
    if (this.currentIndex < nonDeletedFlashcards.length - 1) {
      this.currentIndex++;
      this.currentSide = 'front';
    }
  }

  playAudio(side: 'left' | 'right') {
    if (!this.currentFlashcard) 
      return;
    
    const content = (side === 'left') ? this.currentFlashcard.frontContent : this.currentFlashcard.backContent;
    const contentType = (side === 'left') ? this.currentFlashcard.leftContentType : this.currentFlashcard.rightContentType;
    if (contentType !== 'text')
      throw new Error('Audio can only be played for text content');

    this.service.startPlayingTextPronounciation(content.id);
    console.log(`Playing audio for ${side} side of flashcard ID ${this.currentFlashcard.id}`);
  }
}

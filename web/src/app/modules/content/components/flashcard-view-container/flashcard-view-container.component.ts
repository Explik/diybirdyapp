import { Component, EventEmitter, Input, Output } from '@angular/core';

import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { FormsModule } from '@angular/forms';
import { EditFlashcard, EditFlashcardDeck, EditFlashcardDeckImpl, EditFlashcardImpl, EditFlashcardLanguageImpl } from '../../models/editFlashcard.model';
import { AudioPreviewComponent } from "../audio-preview/audio-preview.component";
import { ImagePreviewComponent } from "../image-preview/image-preview.component";
import { VideoPreviewComponent } from "../video-preview/video-preview.component";
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-flashcard-view-container',
    standalone: true,
    templateUrl: './flashcard-view-container.component.html',
    styleUrl: './flashcard-view-container.component.css',
    imports: [FormsModule, CommonModule, AudioPreviewComponent, ImagePreviewComponent, VideoPreviewComponent]
})
export class FlashcardViewContainerComponent {
  @Input() flashcardDeck: EditFlashcardDeckImpl | undefined = undefined;
}

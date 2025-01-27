import { Component, Input } from '@angular/core';
import { EditFlashcardVideo } from '../../models/editFlashcard.model';

@Component({
  selector: 'app-video-preview',
  imports: [],
  templateUrl: './video-preview.component.html',
  styleUrl: './video-preview.component.css'
})
export class VideoPreviewComponent {
  @Input({required: true}) data!: EditFlashcardVideo;

  constructor() {}

}

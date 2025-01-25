import { Component, Input } from '@angular/core';
import { EditFlashcardImage } from '../../models/editFlashcard.model';

@Component({
  selector: 'app-image-preview',
  imports: [],
  templateUrl: './image-preview.component.html',
  styleUrl: './image-preview.component.css'
})
export class ImagePreviewComponent {
  @Input({required: true}) data!: EditFlashcardImage;

  constructor() {}
}

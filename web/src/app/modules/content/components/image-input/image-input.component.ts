import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EditFlashcardImage, EditFlashcardImageImpl } from '../../models/editFlashcard.model';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: 'app-image-input',
  templateUrl: './image-input.component.html',
  styleUrls: ['./image-input.component.css']
})
export class ImageInputComponent {
  @Input() imageData: EditFlashcardImage | undefined;
  @Output() imageDataChange = new EventEmitter<EditFlashcardImage | undefined>();

  handleFileInput(event: any) {
    const file = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      this.imageData = EditFlashcardImageImpl.createFromFile(file);
      this.imageDataChange.emit(this.imageData);
    }
  }

  handleDrop(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer?.files.length) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('image/')) {
        this.imageData = EditFlashcardImageImpl.createFromFile(file);
        this.imageDataChange.emit(this.imageData);
      }
    }
  }

  allowDrop(event: DragEvent) {
    event.preventDefault();
  }
}

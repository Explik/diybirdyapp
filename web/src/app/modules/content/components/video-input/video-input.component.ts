import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EditFlashcardVideo, EditFlashcardVideoImpl } from '../../models/editFlashcard.model';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: 'app-video-input',
  templateUrl: './video-input.component.html',
  styleUrl: './video-input.component.css'
})
export class VideoInputComponent {
  @Input() videoData: EditFlashcardVideo | undefined;
  @Output() videoDataChange = new EventEmitter<EditFlashcardVideo | undefined>();

  handleFileInput(event: any) {
    const file = event.target.files[0];
    if (file && file.type.startsWith('video/')) {
      this.videoData = EditFlashcardVideoImpl.createFromFile(file);
      this.videoDataChange.emit(this.videoData);
    }
  }

  handleDrop(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer?.files.length) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('video/')) {
        this.videoData = EditFlashcardVideoImpl.createFromFile(file);
        this.videoDataChange.emit(this.videoData);
      }
    }
  }

  allowDrop(event: DragEvent) {
    event.preventDefault();
  }
}

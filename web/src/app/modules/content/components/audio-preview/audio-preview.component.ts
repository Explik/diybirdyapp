import { Component, Host, HostListener, Input } from '@angular/core';
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { IconComponent } from '../../../../shared/components/icon/icon.component';
import { CommonModule } from '@angular/common';
import { EditFlashcardAudio } from '../../models/editFlashcard.model';

@Component({
  selector: 'app-audio-preview',
  imports: [CommonModule, IconComponent],
  templateUrl: './audio-preview.component.html',
  styleUrl: './audio-preview.component.css'
})
export class AudioPreviewComponent {
  @Input({required: true}) data!: EditFlashcardAudio;

  isPlaying: boolean = false;

  constructor(private service: AudioPlayingService) {}

  async playAsync() {
    this.isPlaying = true;
    await this.service.startPlaying(this.data);
    this.isPlaying = false;
  }

  async stopAsync() {
    this.isPlaying = false;
    this.service.stopPlaying();
  }

  @HostListener('click', ['$event'])
  async onClick(e: MouseEvent) {
    e.stopPropagation();

    if (this.isPlaying) {
      await this.stopAsync();
    } else {
      await this.playAsync();
    }
  }
}

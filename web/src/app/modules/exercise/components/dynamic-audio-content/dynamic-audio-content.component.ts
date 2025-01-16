import { Component, Host, HostListener, Input } from '@angular/core';
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { IconComponent } from '../../../../shared/components/icon/icon.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dynamic-audio-content',
  standalone: true,
  imports: [CommonModule, IconComponent],
  templateUrl: './dynamic-audio-content.component.html'
})
export class DynamicAudioContentComponent {
  @Input() data?: ExerciseContentAudioDto; 

  constructor(private audioPlayService: AudioPlayService) {}

  playAudio() {
    if (!this.data?.audioUrl)
      throw new Error('Audio URL is missing');
    
    this.audioPlayService.toggle(this.data.audioUrl);
  }

  @HostListener('click', ['$event'])
  onClick(event: MouseEvent) {
    event.stopPropagation();
    this.playAudio();
  }

  @HostListener('keydown.enter', ['$event'])
  onEnter(event: KeyboardEvent) {
    event.stopPropagation();
    this.playAudio();
  }
}

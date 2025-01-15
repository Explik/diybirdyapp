import { CommonModule } from '@angular/common';
import { Component, HostListener, Input } from '@angular/core';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';

@Component({
  selector: 'app-dynamic-text-content',
  standalone: true,
  imports: [CommonModule, IconComponent],
  templateUrl: './dynamic-text-content.component.html'
})
export class DynamicTextContentComponent {
  @Input() data?: ExerciseContentTextDto;

  constructor(private audioPlayService: AudioPlayService) {}

  playAudioIfAny() {
    if (this.data?.pronunciationUrl)
      this.audioPlayService.toggle(this.data.pronunciationUrl);
  }

  @HostListener('click', ['$event'])
  onClick(event: MouseEvent) {
    event.stopPropagation();
    this.playAudioIfAny();
  }

  @HostListener('keydown.enter', ['$event'])
  onEnter(event: KeyboardEvent) {
    event.stopPropagation();
    this.playAudioIfAny();
  }     
}

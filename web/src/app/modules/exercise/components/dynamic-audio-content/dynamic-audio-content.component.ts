import { Component, HostListener, Input, OnChanges, OnDestroy } from '@angular/core';
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { IconComponent } from '../../../../shared/components/icon/icon.component';

import { ExerciseContentAudioDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-dynamic-audio-content',
  standalone: true,
  imports: [IconComponent],
  templateUrl: './dynamic-audio-content.component.html'
})
export class DynamicAudioContentComponent implements OnChanges, OnDestroy {
  @Input() data?: ExerciseContentAudioDto; 
  @Input() autoplay = true;

  private autoplayUrl?: string;
  private isAutoplaying = false;

  constructor(private audioPlayService: AudioPlayService) {}

  ngOnChanges(): void {
    this.syncAutoplay();
  }

  ngOnDestroy(): void {
    this.stopAutoplay();
  }

  toggleAudio() {
    if (!this.data?.audioUrl)
      throw new Error('Audio URL is missing');
    
    this.audioPlayService.toggle(this.data.audioUrl);
  }

  private syncAutoplay() {
    const nextUrl = this.data?.audioUrl;
    const shouldAutoplay = this.autoplay && !!nextUrl;

    if (this.isAutoplaying && this.autoplayUrl && (!shouldAutoplay || nextUrl !== this.autoplayUrl)) {
      this.audioPlayService.stop(this.autoplayUrl);
      this.isAutoplaying = false;
    }

    if (shouldAutoplay && nextUrl && (!this.isAutoplaying || nextUrl !== this.autoplayUrl)) {
      this.audioPlayService.play(nextUrl);
      this.isAutoplaying = true;
    }

    this.autoplayUrl = nextUrl;
  }

  private stopAutoplay() {
    if (!this.isAutoplaying || !this.autoplayUrl)
      return;

    this.audioPlayService.stop(this.autoplayUrl);
    this.isAutoplaying = false;
  }

  @HostListener('click', ['$event'])
  onClick(event: MouseEvent) {
    event.stopPropagation();
    this.toggleAudio();
  }

  @HostListener('keydown.enter', ['$event'])
  onEnter(event: Event) {
    event.stopPropagation();
    this.toggleAudio();
  }
}

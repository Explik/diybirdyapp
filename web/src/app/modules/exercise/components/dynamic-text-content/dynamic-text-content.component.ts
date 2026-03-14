
import { Component, HostListener, Input, OnChanges, OnDestroy } from '@angular/core';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { ExerciseContentTextDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-dynamic-text-content',
  standalone: true,
  imports: [IconComponent],
  templateUrl: './dynamic-text-content.component.html'
})
export class DynamicTextContentComponent implements OnChanges, OnDestroy {
  @Input() data?: ExerciseContentTextDto;
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

  toggleAudioIfAny() {
    if (this.data?.pronunciationUrl)
      this.audioPlayService.toggle(this.data.pronunciationUrl);
  }

  private syncAutoplay() {
    const nextUrl = this.data?.pronunciationUrl;
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
    this.toggleAudioIfAny();
  }

  @HostListener('keydown.enter', ['$event'])
  onEnter(event: Event) {
    event.stopPropagation();
    this.toggleAudioIfAny();
  }     
}

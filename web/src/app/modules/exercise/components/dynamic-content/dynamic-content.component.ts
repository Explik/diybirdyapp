import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { DynamicAudioContentComponent } from '../dynamic-audio-content/dynamic-audio-content.component';
import { DynamicImageContentComponent } from '../dynamic-image-content/dynamic-image-content.component';
import { DynamicTextContentComponent } from '../dynamic-text-content/dynamic-text-content.component';

@Component({
  selector: 'app-dynamic-content',
  standalone: true,
  imports: [CommonModule, DynamicAudioContentComponent, DynamicImageContentComponent, DynamicTextContentComponent],
  templateUrl: './dynamic-content.component.html'
})
export class DynamicContentComponent {
  @Input() data?: ExerciseContentDto;

  castToAudio(data: ExerciseContentDto): ExerciseContentAudioDto {
    return data as ExerciseContentAudioDto;
  }

  castToImage(data: ExerciseContentDto): ExerciseContentImageDto {
    return data as ExerciseContentImageDto;
  }
  
  castToText(data: ExerciseContentDto): ExerciseContentTextDto {
    return data as ExerciseContentTextDto;
  }
}

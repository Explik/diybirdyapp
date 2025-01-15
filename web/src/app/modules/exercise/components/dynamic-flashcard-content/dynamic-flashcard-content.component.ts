import { Component, Input } from '@angular/core';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { DynamicTextContentComponent } from "../dynamic-text-content/dynamic-text-content.component";
import { CommonModule } from '@angular/common';
import { DynamicImageContentComponent } from '../dynamic-image-content/dynamic-image-content.component';

@Component({
  selector: 'app-dynamic-flashcard-content',
  standalone: true,
  imports: [CommonModule, FlashcardComponent, DynamicTextContentComponent, DynamicImageContentComponent],
  templateUrl: './dynamic-flashcard-content.component.html'
})
export class DynamicFlashcardContentComponent {
  @Input() data?: ExerciseContentFlashcardDto;

  castToText(data: ExerciseContentDto): ExerciseContentTextDto {
    return data as ExerciseContentTextDto;
  }

  castToImage(data: ExerciseContentDto): ExerciseContentImageDto {
    return data as ExerciseContentImageDto;
  }
}

import { Component, Input } from '@angular/core';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { DynamicTextContentComponent } from "../dynamic-text-content/dynamic-text-content.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dynamic-flashcard-content',
  standalone: true,
  imports: [CommonModule, FlashcardComponent, DynamicTextContentComponent],
  templateUrl: './dynamic-flashcard-content.component.html'
})
export class DynamicFlashcardContentComponent {
  @Input() data?: ExerciseContentFlashcardDto;

  castToText(data: ExerciseContentDto): ExerciseContentTextDto {
    return data as ExerciseContentTextDto;
  }
}

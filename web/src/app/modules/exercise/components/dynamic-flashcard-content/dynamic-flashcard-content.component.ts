import { Component, Input } from '@angular/core';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { CommonModule } from '@angular/common';
import { DynamicContentComponent } from '../dynamic-content/dynamic-content.component';
import { ExerciseContentFlashcardDto, ExerciseContentFlashcardSideDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-dynamic-flashcard-content',
  standalone: true,
  imports: [CommonModule, FlashcardComponent, DynamicContentComponent],
  templateUrl: './dynamic-flashcard-content.component.html'
})
export class DynamicFlashcardContentComponent {
  @Input() data?: ExerciseContentFlashcardDto | ExerciseContentFlashcardSideDto; 

  get flashcardData(): ExerciseContentFlashcardDto | undefined {
    if (this.data?.type !== 'flashcard') 
      return undefined; 

    return this.data as ExerciseContentFlashcardDto;
  }

  get flashcardSideData(): ExerciseContentFlashcardSideDto | undefined {
    if (this.data?.type !== 'flashcard-side') 
      return undefined; 

    return this.data as ExerciseContentFlashcardSideDto; 
  }
}

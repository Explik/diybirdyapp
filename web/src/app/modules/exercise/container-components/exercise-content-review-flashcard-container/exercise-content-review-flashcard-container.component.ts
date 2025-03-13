import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from "../../components/instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { RecognizabilityRatingComponent } from "../../../../shared/components/recognizability-rating/recognizability-rating.component";
import { ExerciseService } from '../../services/exercise.service';
import { DynamicFlashcardContentComponent } from '../../components/dynamic-flashcard-content/dynamic-flashcard-content.component';
import { ExerciseContentFlashcardDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-content-review-flashcard-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, DynamicFlashcardContentComponent, RecognizabilityRatingComponent],
  templateUrl: './exercise-content-review-flashcard-container.component.html'
})
export class ExerciseContentReviewFlashcardContainerComponent implements OnInit {
  content?: ExerciseContentFlashcardDto;

  constructor(
    private exerciseService: ExerciseService
  ) { }

  ngOnInit(): void {
    this.exerciseService.getContent<ExerciseContentFlashcardDto>().subscribe(data => this.content = data);
  }

  async handleRatingSelected(rating: string) {
    await this.exerciseService.submitAnswerAsync({ type: "recognizability-rating", rating });
    await this.exerciseService.nextExerciseAsync();
  }
}

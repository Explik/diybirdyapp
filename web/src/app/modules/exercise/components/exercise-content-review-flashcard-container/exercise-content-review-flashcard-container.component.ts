import { Component, OnInit } from '@angular/core';
import { FlashcardContent } from '../../../../shared/models/content.interface';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { RecognizabilityRatingComponent } from "../../../../shared/components/recognizability-rating/recognizability-rating.component";
import { FlashcardReviewComponent } from '../../../flashcard/components/flashcard-review/flashcard-review.component';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseSessionService } from '../../services/exerciseSession.service';

@Component({
  selector: 'app-exercise-content-review-flashcard-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, FlashcardReviewComponent, RecognizabilityRatingComponent],
  templateUrl: './exercise-content-review-flashcard-container.component.html'
})
export class ExerciseContentReviewFlashcardContainerComponent implements OnInit {
  content?: FlashcardContent;

  constructor(
    private exerciseService: ExerciseService,
    private sessionService: ExerciseSessionService
  ) { }

  ngOnInit(): void {
    this.exerciseService.getContent<FlashcardContent>().subscribe(data => this.content = data);
  }

  async handleRatingSelected(rating: string) {
    await this.exerciseService.submitAnswerAsync({ type: "recognizability-rating", rating });
    await this.sessionService.nextExerciseAsync();
  }
}

import { Component } from '@angular/core';
import { TextButtonComponent } from '../../../../shared/components/text-button/text-button.component';
import { CommonModule } from '@angular/common';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseStates } from '../../models/exercise.interface';

@Component({
  selector: 'app-exercise-navigation-check-answer-container',
  standalone: true,
  imports: [TextButtonComponent, CommonModule],
  templateUrl: './exercise-navigation-check-answer-container.component.html'
})
export class ExerciseNavigationCheckAnswerContainerComponent {
  showCheckAnswer = false;

  constructor(private exerciseService: ExerciseService) {
    this.exerciseService.getState().subscribe(state => {
      this.showCheckAnswer = state === ExerciseStates.Unanswered;
    });
  }

  handleNextExercise() {
    this.exerciseService.nextExerciseAsync();
  }

  handleSkipExercise() {
    this.exerciseService.skipExerciseAsync();
  }

  handleCheckAnswer() {
    this.exerciseService.checkAnswerAsync();
  }
}

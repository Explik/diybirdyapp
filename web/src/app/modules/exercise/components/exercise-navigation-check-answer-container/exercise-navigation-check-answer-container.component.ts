import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseStates } from '../../models/exercise.interface';
import { ButtonComponent } from "../../../../shared/components/button/button.component";

@Component({
  selector: 'app-exercise-navigation-check-answer-container',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
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

import { Component } from '@angular/core';
import { TextButtonComponent } from '../../../../shared/components/text-button/text-button.component';
import { ExerciseSessionService } from '../../services/exerciseSession.service';
import { CommonModule } from '@angular/common';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseStates } from '../../models/exercise.interface';

@Component({
  selector: 'app-exercise-navigation-skip-only-container',
  standalone: true,
  imports: [TextButtonComponent, CommonModule],
  templateUrl: './exercise-navigation-skip-only-container.component.html'
})
export class ExerciseNavigationSkipOnlyContainerComponent {
  showSkipExercise = false;

  constructor(
    private exerciseService: ExerciseService,
    private sessionService: ExerciseSessionService) {
      this.exerciseService.getState().subscribe(state => {
        this.showSkipExercise = state === ExerciseStates.Unanswered;
      });
  }

  handleNextExercise() {
    this.sessionService.nextExerciseAsync();
  }

  handleSkipExercise() {
    this.sessionService.skipExerciseAsync();
  }
}

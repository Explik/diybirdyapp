import { Component } from '@angular/core';
import { ExerciseService } from '../../services/exercise.service';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-exercise-navigation-continue-container',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './exercise-navigation-continue-container.component.html'
})
export class ExerciseNavigationContinueContainerComponent {
  constructor(private exerciseService: ExerciseService) {}

  handleContinue() {
    void this.exerciseService.nextExerciseAsync();
  }
}

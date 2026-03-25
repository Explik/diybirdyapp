import { Component, OnDestroy } from '@angular/core';

import { ExerciseService } from '../../services/exercise.service';
import { ExerciseStates } from '../../models/exercise.interface';
import { ButtonComponent } from "../../../../shared/components/button/button.component";
import { Subscription } from 'rxjs';
import { HotkeyService } from '../../../../shared/services/hotKey.service';

@Component({
  selector: 'app-exercise-navigation-check-answer-container',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './exercise-navigation-check-answer-container.component.html'
})
export class ExerciseNavigationCheckAnswerContainerComponent implements OnDestroy {
  private subs = new Subscription();

  showCheckAnswer = false;
  isBusy = false;

  constructor(private exerciseService: ExerciseService, private hotkeyService: HotkeyService) {
    this.exerciseService.getState().subscribe(state => {
      const isAnswered = state !== ExerciseStates.Unanswered;
      this.showCheckAnswer = !isAnswered;

      if (isAnswered) {
        this.subs.add(
          this.hotkeyService.onHotkey({ key: 'enter' }).subscribe(() => this.handleNextExercise())
        );
      }
      else {
        // Unsubscribe from previous hotkeys if the state changes back to unanswered
        this.subs.unsubscribe(); 
      } 
    });

    this.exerciseService.getIsBusy().subscribe(isBusy => {
      this.isBusy = isBusy;
    });
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  handleNextExercise() {
    if (this.isBusy)
      return;

    this.exerciseService.nextExerciseAsync();
  }

  handleSkipExercise() {
    if (this.isBusy)
      return;

    this.exerciseService.skipExerciseAsync();
  }

  handleCheckAnswer() {
    this.exerciseService.checkAnswerAsync();
  }
}

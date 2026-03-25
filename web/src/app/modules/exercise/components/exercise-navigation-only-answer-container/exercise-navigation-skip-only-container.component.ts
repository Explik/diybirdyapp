import { Component, OnDestroy, OnInit } from '@angular/core';

import { ExerciseService } from '../../services/exercise.service';
import { ExerciseStates } from '../../models/exercise.interface';
import { ButtonComponent } from "../../../../shared/components/button/button.component";
import { Subscription } from 'rxjs';
import { HotkeyService } from '../../../../shared/services/hotKey.service';

@Component({
  selector: 'app-exercise-navigation-skip-only-container',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './exercise-navigation-skip-only-container.component.html'
})
export class ExerciseNavigationSkipOnlyContainerComponent implements OnInit, OnDestroy {
  private subs = new Subscription();

  showSkipExercise = false;
  isBusy = false;

  constructor(private exerciseService: ExerciseService, private hotkeyService: HotkeyService) {
    this.exerciseService.getState().subscribe(state => {
      const isAnswered = state !== ExerciseStates.Unanswered;
      this.showSkipExercise = !isAnswered;
    });

    this.exerciseService.getIsBusy().subscribe(isBusy => {
      this.isBusy = isBusy;
    });
  }

  ngOnInit(): void {
    this.subs.add(
      this.hotkeyService.onHotkey({ key: 'enter' }).subscribe(() => { 
        if (!this.showSkipExercise && !this.isBusy) this.handleNextExercise(); 
      })
    ); 
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
}

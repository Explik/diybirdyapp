import { Component, OnDestroy, OnInit } from '@angular/core';
import { ExerciseService } from '../../services/exercise.service';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { HotkeyService } from '../../../../shared/services/hotKey.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-exercise-navigation-continue-container',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './exercise-navigation-continue-container.component.html'
})
export class ExerciseNavigationContinueContainerComponent implements OnInit, OnDestroy {
  private subs = new Subscription();

  constructor(private exerciseService: ExerciseService, private hotkeyService: HotkeyService) {}
  
  ngOnInit(): void {
    this.subs.add(
      this.hotkeyService.onHotkey({ key: 'enter' }).subscribe(() => this.handleContinue())
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  handleContinue() {
    void this.exerciseService.nextExerciseAsync();
  }
}

import { Component, OnInit } from '@angular/core';

import { ProgressBarComponent } from '../../../../shared/components/progress-bar/progress-bar.component';
import { ExerciseService } from '../../services/exercise.service';

@Component({
  selector: 'app-exercise-progress-bar',
  standalone: true,
  imports: [ProgressBarComponent],
  template: '<app-progress-bar [progress]="progress"></app-progress-bar>'
})
export class ExerciseProgressBarComponent implements OnInit {
    progress: number = 0;

    constructor(private exerciseService: ExerciseService) {}

    ngOnInit(): void {
      this.exerciseService.getProgress().subscribe(progressData => {
        this.progress = progressData?.percentage || 0;
      });
    }
}

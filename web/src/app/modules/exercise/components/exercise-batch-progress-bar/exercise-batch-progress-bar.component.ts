import { Component, OnInit } from '@angular/core';
import { BatchProgressBarComponent } from '../../../../shared/components/batch-progress-bar/batch-progress-bar.component';
import { ExerciseService } from '../../services/exercise.service';

@Component({
  selector: 'app-exercise-batch-progress-bar',
  standalone: true,
  imports: [BatchProgressBarComponent],
  template: '<app-batch-progress-bar [progress]="progress" [hasMoreBatches]="hasMoreBatches"></app-batch-progress-bar>'
})
export class ExerciseBatchProgressBarComponent implements OnInit {
    progress: number = 0;
    hasMoreBatches: boolean = false;

    constructor(private exerciseService: ExerciseService) {}

    ngOnInit(): void {
      this.exerciseService.getProgress().subscribe(progressData => {
        this.progress = progressData?.percentage || 0;
        this.hasMoreBatches = progressData?.hasMoreBatches || false;
      });
    }
}

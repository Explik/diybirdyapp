import { Component } from '@angular/core';
import { ExerciseSessionDataService } from '../../services/exerciseSessionData.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-exercise-samples-page',
  imports: [CommonModule],
  templateUrl: './exercise-samples-page.component.html'
})
export class ExerciseSamplesPageComponent {
  exerciseTypes: string[] = []

  constructor(
    private exerciseService: ExerciseSessionDataService,
    private router: Router) {
    this.exerciseService
      .getExerciseTypes()
      .subscribe(data => { this.exerciseTypes = data });
  }

  goToExerciseType(type: string) {
    this.router.navigate(['/exercise', type]);
  }
}

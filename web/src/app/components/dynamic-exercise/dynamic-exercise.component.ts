import { Component } from '@angular/core';
import { BaseExercise } from '../../interfaces/exercise.interface';
import { ActivatedRoute } from '@angular/router';
import { ExerciseService } from '../../services/exercise.service';
import { CommonModule } from '@angular/common';
import { WriteSentenceExerciseUsingWordComponent } from '../write-sentence-using-word-exercise/write-sentence-using-word-exercise.component';

@Component({
  selector: 'app-dynamic-exercise',
  standalone: true,
  imports: [CommonModule, WriteSentenceExerciseUsingWordComponent],
  templateUrl: './dynamic-exercise.component.html'
})
export class DynamicExerciseComponent {
  exerciseId: string | undefined = undefined;
  exerciseType: string | undefined = undefined;
  exerciseData: any | undefined = undefined;

  constructor(
    private route: ActivatedRoute,
    private exerciseService: ExerciseService,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const exerciseId = params.get('id') ?? "1";
      
      this.exerciseService.getExercise(exerciseId).subscribe(data => {
        this.exerciseId = data.id;
        this.exerciseType = data.exerciseType;
        this.exerciseData = data;
      });
    });
  }
}

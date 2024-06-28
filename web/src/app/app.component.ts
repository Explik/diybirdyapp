import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { BaseExercise, MultipleChoiceOption, MultipleTextChoiceOptionExercise, TranslateSentenceExercise, WriteSentenceUsingWordExercise } from './modules/exercise/models/exercise.interface';
import { ExerciseService } from './modules/exercise/services/exercise.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, RouterOutlet, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'diy-birdy-app';
  exercises: BaseExercise[] | undefined = undefined;

  constructor(
    private exerciseService: ExerciseService,
  ) {}

  ngOnInit(): void {
      this.exerciseService.getExercises().subscribe(data => {
        this.exercises = data;
      });
  }

  createWritingExercise() {
    const exercise: WriteSentenceUsingWordExercise = { 
      id: this.getRandomInt(0, 100), 
      exerciseType: "write-sentence-using-word-exercise", 
      word: "random" 
    };
    this.exerciseService.createExercise(exercise).subscribe(this.addExercise);
  }

  createTranslateExercise() {
    const exercise: TranslateSentenceExercise = { 
      id: this.getRandomInt(0, 100), 
      exerciseType: "write-translated-sentence-exercise", 
      originalSentence: "Hello great word",
      targetLanguage: "Danish", 
    };
    this.exerciseService.createExercise(exercise).subscribe(this.addExercise);
  }

  createMultipleExercise() {
    const exercise: MultipleTextChoiceOptionExercise = {
      id: this.getRandomInt(0, 100), 
      exerciseType: "multiple-text-choice-exercise", 
      options: [
        { id: this.getRandomInt(0, 100), text: "Hun" },
        { id: this.getRandomInt(0, 100), text: "Hund" },
        { id: this.getRandomInt(0, 100), text: "Hunlig" },
        { id: this.getRandomInt(0, 100), text: "Hundeliv" },
      ]
    }
    this.exerciseService.createExercise(exercise).subscribe(this.addExercise);
  }

  addExercise(exercise: BaseExercise) {
    this.exercises ??= [];
    this.exercises = [...(this.exercises ?? []), exercise];
  }

  getRandomInt(min: number, max: number): string {
    const minCeiled = Math.ceil(min);
    const maxFloored = Math.floor(max);
    return Math.floor(Math.random() * (maxFloored - minCeiled) + minCeiled) + ""; // The maximum is exclusive and the minimum is inclusive
  }
}

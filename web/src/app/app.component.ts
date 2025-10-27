import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet, Router } from '@angular/router';
import { GenericExercise, MultipleChoiceOption } from './modules/exercise/models/exercise.interface';
import { CommonModule } from '@angular/common';
import { ExerciseService } from './modules/exercise/services/exercise.service';
import { AuthService } from './shared/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, RouterOutlet, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'diy-birdy-app';
  exercises: GenericExercise[] | undefined = undefined;

  constructor(
    private exerciseService: ExerciseService,
    private auth: AuthService,
    private router: Router,
  ) {}

  get isLoggedIn(): boolean {
    return this.auth.isLoggedIn();
  }

  logout(): void {
    // Attempt server logout; on success redirect. On error, still clear local token and redirect.
    this.auth.logout().subscribe({
      next: () => {
        try { this.router.navigate(['/login']); } catch (e) { /* ignore navigation errors */ }
      },
      error: () => {
        localStorage.removeItem('auth_token');
        try { this.router.navigate(['/login']); } catch (e) { /* ignore navigation errors */ }
      }
    });
  }

  ngOnInit(): void {
      // this.exerciseService.getExercises().subscribe(data => {
      //   this.exercises = data;
      // });
  }

  createWritingExercise() {
    // const exercise: WriteSentenceUsingWordExercise = { 
    //   id: this.getRandomInt(0, 100), 
    //   exerciseType: "write-sentence-using-word-exercise", 
    //   word: "random" 
    // };
    // this.exerciseService.createExercise(exercise).subscribe(this.addExercise);
  }

  createTranslateExercise() {
    // const exercise: TranslateSentenceExercise = { 
    //   id: this.getRandomInt(0, 100), 
    //   exerciseType: "write-translated-sentence-exercise", 
    //   originalSentence: "Hello great word",
    //   targetLanguage: "Danish", 
    // };
    // this.exerciseService.createExercise(exercise).subscribe(this.addExercise);
  }

  createMultipleExercise() {
    // const exercise: MultipleTextChoiceOptionExercise = {
    //   id: this.getRandomInt(0, 100), 
    //   exerciseType: "multiple-text-choice-exercise", 
    //   options: [
    //     { id: this.getRandomInt(0, 100), text: "Hun" },
    //     { id: this.getRandomInt(0, 100), text: "Hund" },
    //     { id: this.getRandomInt(0, 100), text: "Hunlig" },
    //     { id: this.getRandomInt(0, 100), text: "Hundeliv" },
    //   ]
    // }
    // this.exerciseService.createExercise(exercise).subscribe(this.addExercise);
  }

  addExercise(exercise: GenericExercise) {
    this.exercises ??= [];
    this.exercises = [...(this.exercises ?? []), exercise];
  }

  getRandomInt(min: number, max: number): string {
    const minCeiled = Math.ceil(min);
    const maxFloored = Math.floor(max);
    return Math.floor(Math.random() * (maxFloored - minCeiled) + minCeiled) + ""; // The maximum is exclusive and the minimum is inclusive
  }
}

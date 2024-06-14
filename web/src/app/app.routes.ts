import { Routes } from '@angular/router';
import { DynamicExerciseComponent } from './components/dynamic-exercise/dynamic-exercise.component';

export const routes: Routes = [
  { path: 'exercise/:id', component: DynamicExerciseComponent },
  { path: '', redirectTo: '/exercise/1', pathMatch: 'full' }
];

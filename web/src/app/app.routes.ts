import { Routes } from '@angular/router';
import { ExercisePageComponent } from './modules/exercise/pages/exercise-page/exercise-page.component';

export const routes: Routes = [
  { path: 'exercise/:id', component: ExercisePageComponent },
  { path: '', redirectTo: '/exercise/1', pathMatch: 'full' }
];

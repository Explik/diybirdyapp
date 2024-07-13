import { Routes } from '@angular/router';
import { ExercisePageComponent } from './modules/exercise/pages/exercise-page/exercise-page.component';
import { ImportModulePageComponent } from './modules/style-library/import-module-page/import-module-page.component';
import { FlashcardPageComponent } from './modules/import/components/flashcard-page/flashcard-page.component';

export const routes: Routes = [
  { path: 'flashcard', component: FlashcardPageComponent },
  { path: 'exercise/:id', component: ExercisePageComponent },
  { path: '', redirectTo: '/exercise/1', pathMatch: 'full' },
  { path: 'library/import', component: ImportModulePageComponent }
];

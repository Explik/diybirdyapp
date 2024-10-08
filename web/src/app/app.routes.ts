import { Routes } from '@angular/router';
import { ExercisePageComponent } from './modules/exercise/pages/exercise-page/exercise-page.component';
import { ImportModulePageComponent } from './modules/style-library/import-module-page/import-module-page.component';
import { FlashcardDeckPageComponent } from './modules/import/components/flashcard-deck-page/flashcard-deck-page.component';
import { FlashcardDecksPageComponent } from './modules/import/components/flashcard-decks-page/flashcard-decks-page.component';

export const routes: Routes = [
  { path: 'flashcard-deck', component: FlashcardDecksPageComponent },
  { path: 'flashcard-deck/:id', component: FlashcardDeckPageComponent },
  { path: 'exercise/:id', component: ExercisePageComponent },
  { path: '', redirectTo: '/exercise/1', pathMatch: 'full' },
  { path: 'library/import', component: ImportModulePageComponent }
];

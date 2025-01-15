import { Routes } from '@angular/router';
import { ExercisePageComponent } from './modules/exercise/pages/exercise-page/exercise-page.component';
import { ImportModulePageComponent } from './modules/style-library/import-module-page/import-module-page.component';
import { FlashcardDeckPageComponent } from './modules/flashcard/components/flashcard-deck-page/flashcard-deck-page.component';
import { FlashcardDecksPageComponent } from './modules/flashcard/components/flashcard-decks-page/flashcard-decks-page.component';
import { SessionPageComponent } from './modules/exercise/pages/session-page/session-page.component';
import { VocabularyPageComponent } from './modules/flashcard/pages/vocabulary-page/vocabulary-page.component';
import { SharedComponentsPageComponent } from './shared/pages/shared-components-page/shared-components-page.component';

export const routes: Routes = [
  { path: '', redirectTo: '/flashcard-deck', pathMatch: 'full' },
  { path: 'flashcard-deck', component: FlashcardDecksPageComponent },
  { path: 'flashcard-deck/:id', component: FlashcardDeckPageComponent },
  { path: 'session/:id', component: SessionPageComponent },
  { path: 'exercise/:id', component: ExercisePageComponent },
  { path: 'library/import', component: ImportModulePageComponent },
  { path: 'vocabulary', component: VocabularyPageComponent },
  { path: 'docs/shared-components', component: SharedComponentsPageComponent }
];

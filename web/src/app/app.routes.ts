import { Routes } from '@angular/router';
import { ExercisePageComponent } from './modules/exercise/pages/exercise-page/exercise-page.component';
import { FlashcardDeckPageComponent } from './modules/content/pages/flashcard-deck-page/flashcard-deck-page.component';
import { FlashcardDecksPageComponent } from './modules/content/pages/flashcard-decks-page/flashcard-decks-page.component';
import { SessionPageComponent } from './modules/exercise/pages/session-page/session-page.component';
import { VocabularyPageComponent } from './modules/content/pages/vocabulary-page/vocabulary-page.component';
import { SharedComponentsPageComponent } from './shared/pages/shared-components-page/shared-components-page.component';
import { ContentComponentsPageComponent } from './modules/content/pages/content-components-page/content-components-page.component';
import { ExerciseComponentsPageComponent } from './modules/exercise/pages/exercise-components-page/exercise-components-page.component';
import { ExerciseSamplesPageComponent } from './modules/exercise/pages/exercise-samples-page/exercise-samples-page.component';

export const routes: Routes = [
  { path: '', redirectTo: '/flashcard-deck', pathMatch: 'full' },
  { path: 'flashcard-deck', component: FlashcardDecksPageComponent },
  { path: 'flashcard-deck/:id', component: FlashcardDeckPageComponent },
  { path: 'session/:id', component: SessionPageComponent },
  { path: 'exercise/:id', component: ExercisePageComponent },
  { path: 'vocabulary', component: VocabularyPageComponent },
  { path: 'docs/shared-components', component: SharedComponentsPageComponent },
  { path: 'docs/content-components', component: ContentComponentsPageComponent },
  { path: 'docs/exercise-components', component: ExerciseComponentsPageComponent },
  { path: 'docs/exercise-samples', component: ExerciseSamplesPageComponent }
];

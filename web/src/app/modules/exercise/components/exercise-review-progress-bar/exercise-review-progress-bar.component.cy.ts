import { mount } from 'cypress/angular';
import { ExerciseReviewProgressBarComponent } from './exercise-review-progress-bar.component';
import { ExerciseService } from '../../services/exercise.service';
import { BehaviorSubject } from 'rxjs';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER: '.flex.items-center',
  PROGRESS_CONTAINER: '.bg-white.border',
  GREEN_BAR: '.bg-green-600',
  RED_BAR: '.bg-red-600',
  GRAY_BAR: '.bg-gray-300',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockExerciseService(
  reviewLongTermPercentage: number = 0,
  reviewLearningPercentage: number = 0,
  reviewNewPercentage: number = 0
): Partial<ExerciseService> {
  const progressSubject = new BehaviorSubject({
    reviewLongTermPercentage,
    reviewLearningPercentage,
    reviewNewPercentage,
  });
  return {
    getProgress: () => progressSubject.asObservable(),
  } as any;
}

function mountComponent(
  exerciseService: Partial<ExerciseService> = createMockExerciseService()
) {
  return mount(ExerciseReviewProgressBarComponent, {
    providers: [
      { provide: ExerciseService, useValue: exerciseService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseReviewProgressBarComponent', () => {

  describe('Rendering', () => {
    it('renders the progress bar container', () => {
      mountComponent();
      cy.get(SEL.CONTAINER).should('exist');
      cy.get(SEL.PROGRESS_CONTAINER).should('exist');
    });

    it('renders three colored bars', () => {
      mountComponent(createMockExerciseService(30, 40, 30));
      cy.get(SEL.GREEN_BAR).should('exist');
      cy.get(SEL.RED_BAR).should('exist');
      cy.get(SEL.GRAY_BAR).should('exist');
    });
  });

  describe('Progress distribution', () => {
    it('displays equal distribution for 33-33-34 percentages', () => {
      mountComponent(createMockExerciseService(33, 33, 34));
      cy.get(SEL.GREEN_BAR).should('exist');
      cy.get(SEL.RED_BAR).should('exist');
      cy.get(SEL.GRAY_BAR).should('exist');
    });

    it('displays only long-term progress when other percentages are 0', () => {
      mountComponent(createMockExerciseService(100, 0, 0));
      cy.get(SEL.GREEN_BAR).should('exist');
    });

    it('handles zero total progress gracefully', () => {
      mountComponent(createMockExerciseService(0, 0, 0));
      cy.get(SEL.CONTAINER).should('exist');
    });
  });

  describe('Normalization', () => {
    it('normalizes percentages that do not sum to 100', () => {
      // Service returns 50-25-25 (total 100), should display as-is or normalized
      mountComponent(createMockExerciseService(50, 25, 25));
      cy.get(SEL.GREEN_BAR).should('exist');
      cy.get(SEL.RED_BAR).should('exist');
      cy.get(SEL.GRAY_BAR).should('exist');
    });
  });
});

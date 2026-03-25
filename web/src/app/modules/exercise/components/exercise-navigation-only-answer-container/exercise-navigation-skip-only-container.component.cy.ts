import { mount } from 'cypress/angular';
import { ExerciseNavigationSkipOnlyContainerComponent } from './exercise-navigation-skip-only-container.component';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseStates } from '../../models/exercise.interface';
import { BehaviorSubject } from 'rxjs';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  SKIP_BUTTON: 'app-button:contains("Skip")',
  CONTINUE_BUTTON: 'app-button:contains("Continue")',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockExerciseService(
  initialState: ExerciseStates = ExerciseStates.Unanswered
): Partial<ExerciseService> {
  const stateSubject = new BehaviorSubject(initialState);
  const transitioningSubject = new BehaviorSubject(false);
  return {
    getState: () => stateSubject.asObservable(),
    getIsBusy: () => transitioningSubject.asObservable(),
    getIsTransitioning: () => transitioningSubject.asObservable(),
    nextExerciseAsync: cy.stub().resolves(),
    skipExerciseAsync: cy.stub().resolves(),
    _testSetState: (state: ExerciseStates) => stateSubject.next(state),
    _testSetTransitioning: (isTransitioning: boolean) => transitioningSubject.next(isTransitioning),
  } as any;
}

function mountComponent(
  exerciseService: Partial<ExerciseService> = createMockExerciseService()
) {
  return mount(ExerciseNavigationSkipOnlyContainerComponent, {
    providers: [
      { provide: ExerciseService, useValue: exerciseService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseNavigationSkipOnlyContainerComponent', () => {

  describe('Button visibility', () => {
    it('shows Skip button when state is Unanswered', () => {
      mountComponent(createMockExerciseService(ExerciseStates.Unanswered));
      cy.get(SEL.SKIP_BUTTON).should('exist');
    });

    it('shows Continue button when state is not Unanswered', () => {
      mountComponent(createMockExerciseService(ExerciseStates.Answered));
      cy.get(SEL.CONTINUE_BUTTON).should('exist');
      cy.get(SEL.SKIP_BUTTON).should('not.exist');
    });
  });

  describe('Button interactions', () => {
    it('calls skipExerciseAsync when Skip button is clicked', () => {
      const service = createMockExerciseService(ExerciseStates.Unanswered);
      mountComponent(service);

      cy.get(SEL.SKIP_BUTTON).click();
      cy.wrap(service.skipExerciseAsync).should('have.been.called');
    });

    it('calls nextExerciseAsync when Continue button is clicked', () => {
      const service = createMockExerciseService(ExerciseStates.Answered);
      mountComponent(service);

      cy.get(SEL.CONTINUE_BUTTON).click();
      cy.wrap(service.nextExerciseAsync).should('have.been.called');
    });
  });
});

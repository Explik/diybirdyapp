import { mount } from 'cypress/angular';
import { ExerciseNavigationCheckAnswerContainerComponent } from './exercise-navigation-check-answer-container.component';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseStates } from '../../models/exercise.interface';
import { BehaviorSubject } from 'rxjs';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CHECK_BUTTON: 'app-button:contains("Check")',
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
  return {
    getState: () => stateSubject.asObservable(),
    nextExerciseAsync: cy.stub().resolves(),
    skipExerciseAsync: cy.stub().resolves(),
    checkAnswerAsync: cy.stub().resolves(),
    _testSetState: (state: ExerciseStates) => stateSubject.next(state),
  } as any;
}

function mountComponent(
  exerciseService: Partial<ExerciseService> = createMockExerciseService()
) {
  return mount(ExerciseNavigationCheckAnswerContainerComponent, {
    providers: [
      { provide: ExerciseService, useValue: exerciseService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseNavigationCheckAnswerContainerComponent', () => {

  describe('Button visibility', () => {
    it('shows Check and Skip buttons when state is Unanswered', () => {
      mountComponent(createMockExerciseService(ExerciseStates.Unanswered));
      cy.get(SEL.CHECK_BUTTON).should('exist');
      cy.get(SEL.SKIP_BUTTON).should('exist');
    });

    it('shows Continue button when state is not Unanswered', () => {
      mountComponent(createMockExerciseService(ExerciseStates.Answered));
      cy.get(SEL.CONTINUE_BUTTON).should('exist');
      cy.get(SEL.CHECK_BUTTON).should('not.exist');
      cy.get(SEL.SKIP_BUTTON).should('not.exist');
    });
  });

  describe('Button interactions', () => {
    it('calls checkAnswerAsync when Check button is clicked', () => {
      const service = createMockExerciseService(ExerciseStates.Unanswered);
      mountComponent(service);

      cy.get(SEL.CHECK_BUTTON).click();
      cy.wrap(service.checkAnswerAsync).should('have.been.called');
    });

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

  describe('State changes', () => {
    it('updates button visibility when state changes from Unanswered to Answered', () => {
      const service = createMockExerciseService(ExerciseStates.Unanswered);
      mountComponent(service);

      cy.get(SEL.CHECK_BUTTON).should('exist');
      
      cy.then(() => {
        (service as any)._testSetState(ExerciseStates.Answered);
      });

      cy.get(SEL.CONTINUE_BUTTON).should('exist');
      cy.get(SEL.CHECK_BUTTON).should('not.exist');
    });
  });
});

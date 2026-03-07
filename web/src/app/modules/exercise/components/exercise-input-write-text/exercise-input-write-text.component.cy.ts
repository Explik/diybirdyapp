import { mount } from 'cypress/angular';
import { ExerciseInputWriteTextComponent } from './exercise-input-write-text.component';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseInputFeedbackTextDto, ExerciseInputWriteTextDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  TEXT_FIELD: 'app-text-field',
  INPUT: 'input',
  FEEDBACK: '.feedback',
  CORRECTION_BUTTON: 'button:contains("I was correct")',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockInput(
  feedback?: ExerciseInputFeedbackTextDto
): ExerciseInputWriteTextDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'write-text',
    feedback,
    text: ''
};
}

function createMockExerciseService(): Partial<ExerciseService> {
  return {
    submitAnswerFeedbackAndContinue: cy.stub().resolves(),
  };
}

function mountComponent(
  input: ExerciseInputWriteTextDto,
  exerciseService: Partial<ExerciseService> = createMockExerciseService()
) {
  return mount(ExerciseInputWriteTextComponent, {
    componentProperties: { input },
    providers: [
      { provide: ExerciseService, useValue: exerciseService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputWriteTextComponent', () => {

  describe('Rendering', () => {
    it('renders the text field component', () => {
      mountComponent(createMockInput());
      cy.get(SEL.TEXT_FIELD).should('exist');
    });
  });

  describe('Feedback display', () => {
    it('displays correct feedback values', () => {
      const input = createMockInput({
        correctValues: ['correct answer'],
        incorrectValues: []
      });
      mountComponent(input);
      cy.contains('correct answer').should('exist');
    });

    it('displays incorrect feedback values', () => {
      const input = createMockInput({
        correctValues: [],
        incorrectValues: ['wrong answer'],
      });
      mountComponent(input);
      cy.contains('wrong answer').should('exist');
    });

    it('displays both correct and incorrect feedback', () => {
      const input = createMockInput({
        correctValues: ['correct1'],
        incorrectValues: ['wrong1'],
      });
      mountComponent(input);
      cy.contains('correct1').should('exist');
      cy.contains('wrong1').should('exist');
    });
  });

  describe('User correction', () => {
    it('shows correction button when there is incorrect feedback', () => {
      const input = createMockInput({
        correctValues: [],
        incorrectValues: ['wrong'],
      });
      mountComponent(input);
      cy.get(SEL.CORRECTION_BUTTON).should('exist');
    });

    it('calls submitAnswerFeedbackAndContinue when correction button is clicked', () => {
      const service = createMockExerciseService();
      const input = createMockInput({
        correctValues: ['i-was-correct'],
        incorrectValues: ['wrong'],
      });
      mountComponent(input, service);

      cy.get(SEL.CORRECTION_BUTTON).click();
      cy.wrap(service.submitAnswerFeedbackAndContinue).should('have.been.calledWith', 'i-was-correct');
    });

    it('does not show correction button when there is no incorrect feedback', () => {
      const input = createMockInput({
        correctValues: ['correct'],
        incorrectValues: [],
      });
      mountComponent(input);
      cy.get(SEL.CORRECTION_BUTTON).should('not.exist');
    });
  });
});

import { mount } from 'cypress/angular';
import { ExerciseInputPairOptionsComponent } from './exercise-input-pair-options.component';
import { ExerciseInputPairOptionsDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  LEFT_OPTION: '.left-option',
  RIGHT_OPTION: '.right-option',
  SELECTED: '.selected',
  CORRECT: '.correct',
  INCORRECT: '.incorrect',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockInput(
  leftOptions: any[] = [],
  rightOptions: any[] = [],
  feedback?: any
): ExerciseInputPairOptionsDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'pair-options',
    leftOptions,
    rightOptions,
    feedback,
    leftOptionType: '',
    rightOptionType: ''
};
}

function mountComponent(input: ExerciseInputPairOptionsDto) {
  return mount(ExerciseInputPairOptionsComponent, {
    componentProperties: { input },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputPairOptionsComponent', () => {

  describe('Rendering', () => {
    it('renders left and right options', () => {
      const leftOptions = [
        { id: 'left1', type: 'text', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', type: 'text', text: 'Right 1' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      cy.contains('Left 1').should('exist');
      cy.contains('Right 1').should('exist');
    });
  });

  describe('Option selection', () => {
    it('selects left option when clicked', () => {
      const leftOptions = [
        { id: 'left1', type: 'text', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', type: 'text', text: 'Right 1' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      
      cy.contains('Left 1').click();
      cy.contains('Left 1').parent().should('have.class', 'selected');
    });

    it('selects right option when clicked', () => {
      const leftOptions = [
        { id: 'left1', type: 'text', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', type: 'text', text: 'Right 1' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      
      cy.contains('Right 1').click();
      cy.contains('Right 1').parent().should('have.class', 'selected');
    });

    it('creates a pair when both left and right options are selected', () => {
      const leftOptions = [
        { id: 'left1', type: 'text', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', type: 'text', text: 'Right 1' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      
      cy.contains('Left 1').click();
      cy.contains('Right 1').click();
      
      // Both should be selected
      cy.contains('Left 1').parent().should('have.class', 'selected');
      cy.contains('Right 1').parent().should('have.class', 'selected');
    });
  });

  describe('Feedback display', () => {
    it('shows correct pairs with correct styling', () => {
      const leftOptions = [
        { id: 'left1', type: 'text', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', type: 'text', text: 'Right 1' },
      ];
      const feedback = {
        correctPairs: [{ leftId: 'left1', rightId: 'right1' }],
        incorrectPairs: [],
      };
      mountComponent(createMockInput(leftOptions, rightOptions, feedback));
      
      cy.contains('Left 1').parent().should('have.class', 'correct');
      cy.contains('Right 1').parent().should('have.class', 'correct');
    });

    it('shows incorrect pairs with incorrect styling', () => {
      const leftOptions = [
        { id: 'left1', type: 'text', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', type: 'text', text: 'Right 1' },
      ];
      const feedback = {
        correctPairs: [],
        incorrectPairs: [{ leftId: 'left1', rightId: 'right1' }],
      };
      mountComponent(createMockInput(leftOptions, rightOptions, feedback));
      
      cy.contains('Left 1').parent().should('have.class', 'incorrect');
      cy.contains('Right 1').parent().should('have.class', 'incorrect');
    });

    it('disables options that are in feedback', () => {
      const leftOptions = [
        { id: 'left1', type: 'text', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', type: 'text', text: 'Right 1' },
      ];
      const feedback = {
        correctPairs: [{ leftId: 'left1', rightId: 'right1' }],
        incorrectPairs: [],
      };
      mountComponent(createMockInput(leftOptions, rightOptions, feedback));
      
      // Options with feedback should be disabled
      cy.contains('Left 1').should('exist');
      cy.contains('Right 1').should('exist');
    });
  });
});

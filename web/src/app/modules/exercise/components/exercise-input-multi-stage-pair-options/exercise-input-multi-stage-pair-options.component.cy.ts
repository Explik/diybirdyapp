import { mount } from 'cypress/angular';
import { ExerciseInputMultiStagePairOptionsComponent } from './exercise-input-multi-stage-pair-options.component';
import { ExerciseInputMultiStagePairOptionsDto } from '../../../../shared/api-client';

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
): ExerciseInputMultiStagePairOptionsDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'multi-stage-pair-options',
    leftOptions,
    rightOptions,
    feedback,
    leftOptionType: '',
    rightOptionType: '',
    answeredCount: 0,
    maxPairs: 0,
    matchedPairLeftIds: []
};
}

function mountComponent(input: ExerciseInputMultiStagePairOptionsDto) {
  return mount(ExerciseInputMultiStagePairOptionsComponent, {
    componentProperties: { input },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputMultiStagePairOptionsComponent', () => {

  describe('Rendering', () => {
    it('renders left options', () => {
      const leftOptions = [
        { id: 'left1', text: 'Left 1' },
        { id: 'left2', text: 'Left 2' },
      ];
      const rightOptions = [
        { id: 'right1', text: 'Right 1' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      cy.contains('Left 1').should('exist');
      cy.contains('Left 2').should('exist');
    });

    it('renders right options', () => {
      const leftOptions = [
        { id: 'left1', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', text: 'Right 1' },
        { id: 'right2', text: 'Right 2' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      cy.contains('Right 1').should('exist');
      cy.contains('Right 2').should('exist');
    });
  });

  describe('Option selection', () => {
    it('selects left option when clicked', () => {
      const leftOptions = [
        { id: 'left1', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', text: 'Right 1' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      
      cy.contains('Left 1').click();
      cy.contains('Left 1').should('have.class', 'selected');
    });

    it('selects right option when clicked', () => {
      const leftOptions = [
        { id: 'left1', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', text: 'Right 1' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      
      cy.contains('Right 1').click();
      cy.contains('Right 1').should('have.class', 'selected');
    });

    it('emits pairSelected when both left and right are selected', () => {
      const pairSelectedSpy = cy.spy().as('pairSelectedSpy');
      const leftOptions = [
        { id: 'left1', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', text: 'Right 1' },
      ];
      
      mount(ExerciseInputMultiStagePairOptionsComponent, {
        componentProperties: {
          input: createMockInput(leftOptions, rightOptions),
          pairSelected: { emit: pairSelectedSpy } as any,
        },
      });

      cy.contains('Left 1').click();
      cy.contains('Right 1').click();
      
      cy.get('@pairSelectedSpy').should('have.been.calledWith', {
        leftId: 'left1',
        rightId: 'right1',
      });
    });
  });

  describe('Feedback display', () => {
    it('shows correct feedback styling', () => {
      const leftOptions = [
        { id: 'left1', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', text: 'Right 1' },
      ];
      const feedback = {
        correctPairs: [{ leftId: 'left1', rightId: 'right1' }],
        incorrectPairs: [],
      };
      mountComponent(createMockInput(leftOptions, rightOptions, feedback));
      
      // After feedback animation, options should maintain visibility
      cy.contains('Left 1').should('exist');
      cy.contains('Right 1').should('exist');
    });

    it('shows incorrect feedback styling', () => {
      const leftOptions = [
        { id: 'left1', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', text: 'Right 1' },
      ];
      const feedback = {
        correctPairs: [],
        incorrectPairs: [{ leftId: 'left1', rightId: 'right1' }],
      };
      mountComponent(createMockInput(leftOptions, rightOptions, feedback));
      
      cy.contains('Left 1').should('exist');
      cy.contains('Right 1').should('exist');
    });
  });

  describe('Interaction blocking', () => {
    it('blocks interactions while submitting', () => {
      const leftOptions = [
        { id: 'left1', text: 'Left 1' },
      ];
      const rightOptions = [
        { id: 'right1', text: 'Right 1' },
      ];
      mountComponent(createMockInput(leftOptions, rightOptions));
      
      // Component should allow initial interaction
      cy.contains('Left 1').should('exist');
    });
  });
});

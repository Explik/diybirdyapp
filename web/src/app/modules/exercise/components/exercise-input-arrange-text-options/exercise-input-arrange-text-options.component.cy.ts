import { mount } from 'cypress/angular';
import { ExerciseInputArrangeTextOptionsComponent } from './exercise-input-arrange-text-options.component';
import { ExerciseInputArrangeTextOptionsDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  OPTION_BUTTON: '.option-button',
  SELECTED_WORD: '.selected-word',
  EMPTY_SLOT: '.empty-slot',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockInput(
  options: any[] = []
): ExerciseInputArrangeTextOptionsDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'arrange-text-options',
    options,
  };
}

function mountComponent(input?: ExerciseInputArrangeTextOptionsDto) {
  return mount(ExerciseInputArrangeTextOptionsComponent, {
    componentProperties: { input },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputArrangeTextOptionsComponent', () => {

  describe('Rendering', () => {
    it('renders available word options', () => {
      const options = [
        { id: 'opt1', text: 'Hello' },
        { id: 'opt2', text: 'World' },
      ];
      mountComponent(createMockInput(options));
      cy.contains('Hello').should('exist');
      cy.contains('World').should('exist');
    });

    it('renders empty slots for unselected words', () => {
      const options = [
        { id: 'opt1', text: 'Hello' },
        { id: 'opt2', text: 'World' },
      ];
      mountComponent(createMockInput(options));
      cy.get(SEL.EMPTY_SLOT).should('have.length', 2);
    });

    it('renders with no options', () => {
      mountComponent(createMockInput([]));
      cy.get(SEL.OPTION_BUTTON).should('not.exist');
    });
  });

  describe('Word selection', () => {
    it('adds word to selected list when clicked', () => {
      const options = [
        { id: 'opt1', text: 'Hello' },
      ];
      mountComponent(createMockInput(options));
      
      cy.contains('Hello').click();
      cy.get(SEL.SELECTED_WORD).should('contain.text', 'Hello');
    });

    it('removes word from available options after selection', () => {
      const options = [
        { id: 'opt1', text: 'Hello' },
      ];
      mountComponent(createMockInput(options));
      
      cy.contains('Hello').click();
      cy.get(SEL.OPTION_BUTTON).should('not.contain', 'Hello');
    });

    it('does not add the same word twice', () => {
      const options = [
        { id: 'opt1', text: 'Hello' },
      ];
      mountComponent(createMockInput(options));
      
      cy.contains('Hello').click();
      cy.get(SEL.SELECTED_WORD).should('have.length', 1);
    });
  });

  describe('Word removal', () => {
    it('removes word from selected list when clicked', () => {
      const options = [
        { id: 'opt1', text: 'Hello' },
      ];
      mountComponent(createMockInput(options));
      
      cy.contains('Hello').click();
      cy.get(SEL.SELECTED_WORD).click();
      cy.get(SEL.SELECTED_WORD).should('not.exist');
    });

    it('adds word back to available options after removal', () => {
      const options = [
        { id: 'opt1', text: 'Hello' },
      ];
      mountComponent(createMockInput(options));
      
      cy.contains('Hello').click();
      cy.get(SEL.SELECTED_WORD).click();
      cy.contains('Hello').should('exist');
    });
  });

  describe('Empty slots', () => {
    it('shows correct number of empty slots', () => {
      const options = [
        { id: 'opt1', text: 'One' },
        { id: 'opt2', text: 'Two' },
        { id: 'opt3', text: 'Three' },
      ];
      mountComponent(createMockInput(options));
      
      cy.get(SEL.EMPTY_SLOT).should('have.length', 3);
      
      cy.contains('One').click();
      cy.get(SEL.EMPTY_SLOT).should('have.length', 2);
    });
  });
});

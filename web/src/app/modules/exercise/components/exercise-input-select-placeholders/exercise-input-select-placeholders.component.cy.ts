import { mount } from 'cypress/angular';
import { ExerciseInputSelectPlaceholdersComponent } from './exercise-input-select-placeholders.component';
import { ExerciseInputSelectPlaceholdersDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  PLACEHOLDER: '.placeholder',
  OPTION: '.option',
  TEXT_PART: '.text-part',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockInput(
  parts: any[] = [],
  options: any[] = []
): ExerciseInputSelectPlaceholdersDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'select-placeholders',
    parts,
    options,
  };
}

function mountComponent(input: ExerciseInputSelectPlaceholdersDto) {
  return mount(ExerciseInputSelectPlaceholdersComponent, {
    componentProperties: { input },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputSelectPlaceholdersComponent', () => {

  describe('Rendering', () => {
    it('renders text parts', () => {
      const parts = [
        { type: 'text', value: 'The cat' },
      ];
      const options = [
        { id: 'opt1', text: 'is' },
      ];
      mountComponent(createMockInput(parts, options));
      cy.contains('The cat').should('exist');
    });

    it('renders placeholder parts', () => {
      const parts = [
        { type: 'placeholder', value: undefined },
      ];
      const options = [
        { id: 'opt1', text: 'option1' },
      ];
      mountComponent(createMockInput(parts, options));
      cy.get(SEL.PLACEHOLDER).should('exist');
    });

    it('renders available options', () => {
      const parts = [
        { type: 'placeholder', value: undefined },
      ];
      const options = [
        { id: 'opt1', text: 'option1' },
        { id: 'opt2', text: 'option2' },
      ];
      mountComponent(createMockInput(parts, options));
      cy.contains('option1').should('exist');
      cy.contains('option2').should('exist');
    });
  });

  describe('Mixed content', () => {
    it('renders text and placeholder parts together', () => {
      const parts = [
        { type: 'text', value: 'Hello' },
        { type: 'placeholder', value: undefined },
        { type: 'text', value: 'world' },
      ];
      const options = [
        { id: 'opt1', text: 'beautiful' },
      ];
      mountComponent(createMockInput(parts, options));
      cy.contains('Hello').should('exist');
      cy.contains('world').should('exist');
      cy.get(SEL.PLACEHOLDER).should('exist');
    });
  });

  describe('Placeholder sizing', () => {
    it('calculates placeholder size based on longest option', () => {
      const parts = [
        { type: 'placeholder', value: undefined },
      ];
      const options = [
        { id: 'opt1', text: 'short' },
        { id: 'opt2', text: 'very long option text' },
      ];
      mountComponent(createMockInput(parts, options));
      cy.get(SEL.PLACEHOLDER).should('exist');
    });
  });

  // Note: Drag-and-drop testing requires more complex setup
  // and is better suited for E2E tests
});

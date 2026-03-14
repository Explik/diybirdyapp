import { mount } from 'cypress/angular';
import { ExerciseInputWritePlaceholdersComponent } from './exercise-input-write-placeholders.component';
import { ExerciseInputWritePlaceholdersDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  TEXT_PART: '.text-part',
  INPUT: 'input',
  FEEDBACK: '.feedback',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockInput(
  parts: any[] = [],
  feedback?: any
): ExerciseInputWritePlaceholdersDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'write-placeholders',
    parts,
    feedback,
  };
}

function mountComponent(input: ExerciseInputWritePlaceholdersDto) {
  return mount(ExerciseInputWritePlaceholdersComponent, {
    componentProperties: { input },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputWritePlaceholdersComponent', () => {

  describe('Rendering', () => {
    it('renders text parts', () => {
      const parts = [
        { type: 'text', value: 'The cat' },
      ];
      mountComponent(createMockInput(parts));
      cy.contains('The cat').should('exist');
    });

    it('renders placeholder input fields', () => {
      const parts = [
        { type: 'placeholder', value: '' },
      ];
      mountComponent(createMockInput(parts));
      cy.get(SEL.INPUT).should('exist');
    });

    it('renders mixed text and placeholder parts', () => {
      const parts = [
        { type: 'text', value: 'Hello' },
        { type: 'placeholder', value: '' },
        { type: 'text', value: 'world' },
      ];
      mountComponent(createMockInput(parts));
      cy.contains('Hello').should('exist');
      cy.contains('world').should('exist');
      cy.get(SEL.INPUT).should('exist');
    });
  });

  describe('User input', () => {
    it('allows typing in placeholder fields', () => {
      const parts = [
        { type: 'placeholder', value: '' },
      ];
      mountComponent(createMockInput(parts));
      cy.get(SEL.INPUT).type('test input');
      cy.get(SEL.INPUT).should('have.value', 'test input');
    });

    it('pre-fills placeholder with existing value', () => {
      const parts = [
        { type: 'placeholder', value: 'existing' },
      ];
      mountComponent(createMockInput(parts));
      cy.get(SEL.INPUT).should('have.value', 'existing');
    });
  });

  describe('Feedback display', () => {
    it('displays feedback when provided', () => {
      const parts = [
        { type: 'placeholder', value: 'answer', id: 'p1' },
      ];
      const feedback = {
        placeholderFeedbacks: [
          {
            placeholderId: 'p1',
            correctValues: ['correct answer'],
            incorrectValues: [],
          },
        ],
      };
      mountComponent(createMockInput(parts, feedback));
      cy.contains('correct answer').should('exist');
    });
  });
});

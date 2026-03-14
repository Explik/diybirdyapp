import { mount } from 'cypress/angular';
import { TextInputComponent } from './text-input.component';
import { EditFlashcardTextImpl } from '../../models/editFlashcard.model';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER: '.text-input-container',
  INPUT:     '.text-input-container input',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createTextData(text: string): EditFlashcardTextImpl {
  const td = EditFlashcardTextImpl.create();
  td.text = text;
  return td;
}

function mountComponent(textData?: EditFlashcardTextImpl) {
  return mount(TextInputComponent, {
    componentProperties: { textData },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('TextInputComponent', () => {

  describe('Rendering', () => {
    it('renders the text-input container', () => {
      mountComponent();
      cy.get(SEL.CONTAINER).should('exist');
    });

    it('renders an input element', () => {
      mountComponent();
      cy.get(SEL.INPUT).should('exist');
    });
  });

  describe('Initial value', () => {
    it('pre-fills the input with the text from textData when provided', () => {
      const textData = createTextData('Hello');
      mountComponent(textData);
      cy.get(SEL.INPUT).should('have.value', 'Hello');
    });

    it('renders an empty input when no textData is provided', () => {
      mountComponent(undefined);
      cy.get(SEL.INPUT).should('have.value', '');
    });
  });

  describe('User input', () => {
    it('updates the displayed value when the user types', () => {
      mountComponent(createTextData(''));
      cy.get(SEL.INPUT).clear().type('New text');
      cy.get(SEL.INPUT).should('have.value', 'New text');
    });

    it('emits textDataChange with updated text when the user types', () => {
      const spy = cy.spy().as('textDataChangeSpy');
      mount(TextInputComponent, {
        componentProperties: {
          textData: createTextData(''),
          textDataChange: { emit: spy } as any,
        },
      });

      cy.get(SEL.INPUT).clear().type('Updated');
      cy.get('@textDataChangeSpy').should('have.been.called');
    });
  });
});

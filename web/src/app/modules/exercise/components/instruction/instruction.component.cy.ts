import { mount } from 'cypress/angular';
import { InstructionComponent } from './instruction.component';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  TITLE: 'h2',
  INSTRUCTION: 'p',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function mountComponent(title: string = '', instruction: string = '') {
  return mount(InstructionComponent, {
    componentProperties: { title, instruction },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('InstructionComponent', () => {

  describe('Rendering', () => {
    it('renders the title', () => {
      const testTitle = 'Exercise Instructions';
      mountComponent(testTitle, 'Follow these steps');
      cy.get(SEL.TITLE).should('contain.text', testTitle);
    });

    it('renders the instruction text', () => {
      const testInstruction = 'Click on the correct answer';
      mountComponent('Title', testInstruction);
      cy.get(SEL.INSTRUCTION).should('contain.text', testInstruction);
    });

    it('renders with empty title', () => {
      mountComponent('', 'Instruction');
      cy.get(SEL.TITLE).should('exist');
    });

    it('renders with empty instruction', () => {
      mountComponent('Title', '');
      cy.get(SEL.INSTRUCTION).should('exist');
    });
  });
});

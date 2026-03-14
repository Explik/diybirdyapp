import { mount } from 'cypress/angular';
import { InfoBoxComponent } from './info-box.component';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER: '.max-w-md',
  TITLE: 'h3',
  DESCRIPTION: 'p',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function mountComponent(title?: string, description?: string) {
  return mount(InfoBoxComponent, {
    componentProperties: { title, description },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('InfoBoxComponent', () => {

  describe('Rendering', () => {
    it('renders the info box container', () => {
      mountComponent('Test Title', 'Test Description');
      cy.get(SEL.CONTAINER).should('exist');
    });

    it('displays the title when provided', () => {
      const testTitle = 'Important Information';
      mountComponent(testTitle, 'Description');
      cy.get(SEL.TITLE).should('contain.text', testTitle);
    });

    it('displays the description when provided', () => {
      const testDescription = 'This is a detailed description';
      mountComponent('Title', testDescription);
      cy.get(SEL.DESCRIPTION).should('contain.text', testDescription);
    });

    it('renders without title when not provided', () => {
      mountComponent(undefined, 'Description');
      cy.get(SEL.CONTAINER).should('exist');
    });

    it('renders without description when not provided', () => {
      mountComponent('Title', undefined);
      cy.get(SEL.CONTAINER).should('exist');
    });
  });
});

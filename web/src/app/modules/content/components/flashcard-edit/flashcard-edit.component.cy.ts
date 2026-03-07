import { mount } from 'cypress/angular';
import { Component, EventEmitter } from '@angular/core';
import { FlashcardEditComponent } from './flashcard-edit.component';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER: '.flashcard-edit-item',
  LEFT_SIDE:  '.left-side',
  RIGHT_SIDE: '.right-side',
} as const;

const PROJECTED = {
  HEADER_TEXT: 'Test Header',
  LEFT_TEXT:   'Front Content',
  RIGHT_TEXT:  'Back Content',
} as const;

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('FlashcardEditComponent', () => {

  describe('Rendering', () => {
    it('renders the flashcard edit container', () => {
      mount(FlashcardEditComponent);
      cy.get(SEL.CONTAINER).should('exist');
    });

    it('renders the left-side slot', () => {
      mount(FlashcardEditComponent);
      cy.get(SEL.LEFT_SIDE).should('exist');
    });

    it('renders the right-side slot', () => {
      mount(FlashcardEditComponent);
      cy.get(SEL.RIGHT_SIDE).should('exist');
    });
  });

  describe('Content projection', () => {
    it('projects header content into the header slot', () => {
      @Component({
        selector: 'app-test-host',
        standalone: true,
        imports: [FlashcardEditComponent],
        template: `
          <app-flashcard-edit>
            <span header>${PROJECTED.HEADER_TEXT}</span>
          </app-flashcard-edit>
        `,
      })
      class TestHostComponent {}

      mount(TestHostComponent);
      cy.contains(PROJECTED.HEADER_TEXT).should('exist');
    });

    it('projects content into the left slot', () => {
      @Component({
        selector: 'app-test-host',
        standalone: true,
        imports: [FlashcardEditComponent],
        template: `
          <app-flashcard-edit>
            <span left>${PROJECTED.LEFT_TEXT}</span>
          </app-flashcard-edit>
        `,
      })
      class TestHostComponent {}

      mount(TestHostComponent);
      cy.get(SEL.LEFT_SIDE).should('contain.text', PROJECTED.LEFT_TEXT);
    });

    it('projects content into the right slot', () => {
      @Component({
        selector: 'app-test-host',
        standalone: true,
        imports: [FlashcardEditComponent],
        template: `
          <app-flashcard-edit>
            <span right>${PROJECTED.RIGHT_TEXT}</span>
          </app-flashcard-edit>
        `,
      })
      class TestHostComponent {}

      mount(TestHostComponent);
      cy.get(SEL.RIGHT_SIDE).should('contain.text', PROJECTED.RIGHT_TEXT);
    });
  });
});

import { mount } from 'cypress/angular';
import { Component } from '@angular/core';
import { FlashcardReviewComponent } from './flashcard-review.component';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CARD:        '.flashcard',
  FRONT_SIDE:  '.flashcard-front',
  BACK_SIDE:   '.flashcard-back',
} as const;

const CONTENT = {
  LEFT:  'Front side content',
  RIGHT: 'Back side content',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function mountComponent(flippable = true) {
  return mount(FlashcardReviewComponent, {
    componentProperties: { flippable },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('FlashcardReviewComponent', () => {

  describe('Rendering', () => {
    it('renders the flashcard element', () => {
      mountComponent();
      cy.get(SEL.CARD).should('exist');
    });

    it('does not have the flipped class initially', () => {
      mountComponent();
      cy.get(SEL.CARD).should('not.have.class', 'flipped');
    });

    it('renders front and back sides', () => {
      mountComponent();
      cy.get(SEL.FRONT_SIDE).should('exist');
      cy.get(SEL.BACK_SIDE).should('exist');
    });
  });

  describe('Content projection', () => {
    it('projects content into the left (front) slot', () => {
      @Component({
        selector: 'app-test-host',
        standalone: true,
        imports: [FlashcardReviewComponent],
        template: `
          <app-flashcard-review>
            <span left>${CONTENT.LEFT}</span>
          </app-flashcard-review>
        `,
      })
      class TestHostComponent {}

      mount(TestHostComponent);
      cy.get(SEL.FRONT_SIDE).should('contain.text', CONTENT.LEFT);
    });

    it('projects content into the right (back) slot', () => {
      @Component({
        selector: 'app-test-host',
        standalone: true,
        imports: [FlashcardReviewComponent],
        template: `
          <app-flashcard-review>
            <span right>${CONTENT.RIGHT}</span>
          </app-flashcard-review>
        `,
      })
      class TestHostComponent {}

      mount(TestHostComponent);
      cy.get(SEL.BACK_SIDE).should('contain.text', CONTENT.RIGHT);
    });
  });

  describe('Flip behavior', () => {
    it('adds the flipped class when the card is clicked', () => {
      mountComponent();
      cy.get(SEL.CARD).click();

      cy.get(SEL.CARD).should('have.class', 'flipped');
    });

    it('removes the flipped class when the card is clicked a second time', () => {
      mountComponent();
      cy.get(SEL.CARD).click();
      cy.get(SEL.CARD).click();

      cy.get(SEL.CARD).should('not.have.class', 'flipped');
    });

    it('does not flip the card when flippable is false', () => {
      mountComponent(false);

      cy.get(SEL.CARD).click();
      
      cy.get(SEL.CARD).should('not.have.class', 'flipped');
    });
  });
});

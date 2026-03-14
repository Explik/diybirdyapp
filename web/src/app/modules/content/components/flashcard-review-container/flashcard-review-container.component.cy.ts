import { mount } from 'cypress/angular';
import { FlashcardReviewContainerComponent } from './flashcard-review-container.component';
import { AudioPlayingService } from '../../services/audioPlaying.service';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER: '.flashcard-review-container',
} as const;

const BTN = {
  PREV: '←',
  NEXT: '→',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockPlayingService(): Partial<AudioPlayingService> {
  return {
    startPlayingTextPronounciation: cy.stub().returns(Promise.resolve()),
    startPlayingEditFlashcard: cy.stub().resolves(),
    stopPlaying: cy.stub(),
  };
}

function createTextFlashcard(frontText: string, backText: string, state = 'unchanged') {
  return {
    state,
    leftContentType: 'text',
    rightContentType: 'text',
    leftTextContent: { text: frontText },
    rightTextContent: { text: backText },
  };
}

function mountComponent(flashcards: any[] = [], playService = createMockPlayingService()) {
  return mount(FlashcardReviewContainerComponent, {
    componentProperties: { flashcards },
    providers: [
      { provide: AudioPlayingService, useValue: playService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('FlashcardReviewContainerComponent', () => {

  describe('Empty state', () => {
    it('renders nothing when no flashcards are provided', () => {
      mountComponent([]);
      cy.get(SEL.CONTAINER).should('not.exist');
    });
  });

  describe('Flashcard display', () => {
    it('renders the container when a flashcard is available', () => {
      const card = createTextFlashcard('Hello', 'Hej');
      mountComponent([card]);
      cy.get(SEL.CONTAINER).should('exist');
    });

    it('shows the front text content of the current flashcard', () => {
      const card = createTextFlashcard('Hello', 'Hej');
      mountComponent([card]);
      cy.get(SEL.CONTAINER).should('contain.text', 'Hello');
    });
  });

  describe('Navigation', () => {
    it('navigates to the next flashcard when the next button is clicked', () => {
      const cards = [
        createTextFlashcard('First front', 'First back'),
        createTextFlashcard('Second front', 'Second back'),
      ];
      mountComponent(cards);

      cy.contains('button', BTN.NEXT).click();

      cy.get(SEL.CONTAINER).should('contain.text', 'Second front');
    });

    it('navigates back to the first flashcard when the previous button is clicked', () => {
      const cards = [
        createTextFlashcard('First front', 'First back'),
        createTextFlashcard('Second front', 'Second back'),
      ];
      mountComponent(cards);

      cy.contains('button', BTN.NEXT).click();
      cy.contains('button', BTN.PREV).click();
      
      cy.get(SEL.CONTAINER).should('contain.text', 'First front');
    });

    it('disables the previous button on the first flashcard', () => {
      const card = createTextFlashcard('Hello', 'Hej');
      mountComponent([card]);

      cy.contains('button', BTN.PREV).should('be.disabled');
    });

    it('disables the next button on the last flashcard', () => {
      const card = createTextFlashcard('Hello', 'Hej');
      mountComponent([card]);

      cy.contains('button', BTN.NEXT).should('be.disabled');
    });
  });

  describe('Deleted flashcard filtering', () => {
    it('skips deleted flashcards and shows only non-deleted ones', () => {
      const cards = [
        createTextFlashcard('Visible', 'Back', 'unchanged'),
        createTextFlashcard('Deleted', 'Back', 'deleted'),
      ];
      mountComponent(cards);

      cy.get(SEL.CONTAINER).should('contain.text', 'Visible');
      cy.get(SEL.CONTAINER).should('not.contain.text', 'Deleted');
    });
  });
});

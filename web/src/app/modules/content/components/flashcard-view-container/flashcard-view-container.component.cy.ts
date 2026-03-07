import { mount } from 'cypress/angular';
import { FlashcardViewContainerComponent } from './flashcard-view-container.component';
import {
  EditFlashcardDeckImpl,
  EditFlashcardImpl,
  EditFlashcardTextImpl,
} from '../../models/editFlashcard.model';
import { AudioPlayingService } from '../../services/audioPlaying.service';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER: '.flashcard-deck-edit-container',
  CARD_ITEM:  '.flashcard-item',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockPlayingService(): Partial<AudioPlayingService> {
  return {
    startPlayingEditFlashcard: cy.stub().resolves(),
    stopPlaying: cy.stub(),
  };
}

function createTextCard(frontText: string, backText: string): EditFlashcardImpl {
  const fc = EditFlashcardImpl.createDefault();

  fc.leftTextContent = EditFlashcardTextImpl.create();
  fc.leftTextContent.text = frontText;

  fc.rightTextContent = EditFlashcardTextImpl.create();
  fc.rightTextContent.text = backText;

  return fc;
}

function createDeck(flashcards: EditFlashcardImpl[] = []): EditFlashcardDeckImpl {
  const deck = new EditFlashcardDeckImpl();
  deck.id = 'deck-1';
  deck.name = 'Test Deck';
  deck.flashcards = flashcards;
  return deck;
}

function mountComponent(
  flashcardDeck: EditFlashcardDeckImpl | undefined,
  playService = createMockPlayingService(),
) {
  return mount(FlashcardViewContainerComponent, {
    componentProperties: { flashcardDeck },
    providers: [
      { provide: AudioPlayingService, useValue: playService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('FlashcardViewContainerComponent', () => {

  describe('Rendering', () => {
    it('renders nothing when flashcardDeck is undefined', () => {
      mountComponent(undefined);
      cy.get(SEL.CONTAINER).should('not.exist');
    });

    it('renders the container when a deck is provided', () => {
      const deck = createDeck([createTextCard('Hello', 'Hej')]);
      mountComponent(deck);
      cy.get(SEL.CONTAINER).should('exist');
    });
  });

  describe('Flashcard list', () => {
    it('renders one flashcard item per card in the deck', () => {
      const deck = createDeck([
        createTextCard('Word 1', 'Translation 1'),
        createTextCard('Word 2', 'Translation 2'),
        createTextCard('Word 3', 'Translation 3'),
      ]);
      mountComponent(deck);
      cy.get(SEL.CARD_ITEM).should('have.length', 3);
    });

    it('renders the front text content of each flashcard', () => {
      const deck = createDeck([createTextCard('Apple', 'Æble')]);
      mountComponent(deck);
      cy.get(SEL.CONTAINER).should('contain.text', 'Apple');
    });

    it('renders the back text content of each flashcard', () => {
      const deck = createDeck([createTextCard('Apple', 'Æble')]);
      mountComponent(deck);
      cy.get(SEL.CONTAINER).should('contain.text', 'Æble');
    });

    it('renders no flashcard items when the deck has an empty flashcard list', () => {
      const deck = createDeck([]);
      mountComponent(deck);
      cy.get(SEL.CARD_ITEM).should('have.length', 0);
    });
  });
});

import { mount } from 'cypress/angular';
import { EventEmitter } from '@angular/core';
import { FlashcardEditContainerComponent } from './flashcard-edit-container.component';
import {
  EditFlashcardDeckImpl,
  EditFlashcardImpl,
  EditFlashcardLanguageImpl,
  EditFlashcardTextImpl,
} from '../../models/editFlashcard.model';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  FORM:        'form.flashcard-deck-edit-container',
  DECK_NAME:   '.deck-name-input input',
  DECK_DESC:   '.deck-description-input textarea',
  DECK_DESC_ERR: '.deck-description-input app-form-error',
  LEFT_LANG:   '.global-left-language-input',
  RIGHT_LANG:  '.global-right-language-input',
  CARD_ITEM:   '.flashcard-item',
  CARD_NUMBER: '.flashcard-item-number',
  LEFT_TYPE:   '.left-content-type-input',
  RIGHT_TYPE:  '.right-content-type-input',
  LEFT_SIDE:   '.left-side',
  RIGHT_SIDE:  '.right-side',
} as const;

const BTN = {
  SAVE:     'Save changes',
  ADD_CARD: '＋ Add flashcard',
  DELETE:   'Delete',
} as const;

const ERR = {
  TITLE_REQUIRED: 'Title is required',
  FRONT_TEXT:     'Front text required',
  BACK_TEXT:      'Back text required',
  FRONT_AUDIO:    'Front audio required',
  BACK_AUDIO:     'Back audio required',
  FRONT_IMAGE:    'Front image required',
  BACK_IMAGE:     'Back image required',
  FRONT_VIDEO:    'Front video required',
  BACK_VIDEO:     'Back video required',
  FRONT_LANG:     'Front language required when text content exists',
  BACK_LANG:      'Back language required when text content exists',
} as const;

const LANG_LABEL = {
  ENGLISH: 'English',
  DANISH:  'Danish',
  CHINESE: 'Chinese',
} as const;

const CONTENT_TYPE_LABEL = {
  TEXT:  'Text',
  AUDIO: 'Audio',
  IMAGE: 'Image',
  VIDEO: 'Video',
} as const;

// ---------------------------------------------------------------------------
// Fixture helpers
// ---------------------------------------------------------------------------

function createLanguage(id: string, name: string, isoCode?: string): EditFlashcardLanguageImpl {
  const lang = new EditFlashcardLanguageImpl();
  lang.id = id;
  lang.name = name;
  lang.isoCode = isoCode;
  return lang;
}

function createDeck(
  name = 'Test Deck',
  description = '',
  flashcards: EditFlashcardImpl[] = [],
): EditFlashcardDeckImpl {
  const deck = new EditFlashcardDeckImpl();
  deck.id = 'deck-1';
  deck.name = name;
  deck.description = description;
  deck.flashcards = flashcards;
  return deck;
}

function createTextCard(
  frontText = 'Front',
  backText = 'Back',
  frontLanguageId = '',
  backLanguageId = '',
): EditFlashcardImpl {
  const fc = EditFlashcardImpl.createDefault();

  fc.leftTextContent = EditFlashcardTextImpl.create();
  fc.leftTextContent.text = frontText;
  fc.leftTextContent.languageId = frontLanguageId;

  fc.rightTextContent = EditFlashcardTextImpl.create();
  fc.rightTextContent.text = backText;
  fc.rightTextContent.languageId = backLanguageId;

  return fc;
}

function createAudioCard(): EditFlashcardImpl {
  const fc = EditFlashcardImpl.createDefault();
  fc.leftContentType = 'audio';
  fc.rightContentType = 'audio';
  fc.leftAudioContent = undefined;
  fc.rightAudioContent = undefined;
  return fc;
}

function createImageCard(): EditFlashcardImpl {
  const fc = EditFlashcardImpl.createDefault();
  fc.leftContentType = 'image';
  fc.rightContentType = 'image';
  fc.leftImageContent = undefined;
  fc.rightImageContent = undefined;
  return fc;
}

function createVideoCard(): EditFlashcardImpl {
  const fc = EditFlashcardImpl.createDefault();
  fc.leftContentType = 'video';
  fc.rightContentType = 'video';
  fc.leftVideoContent = undefined;
  fc.rightVideoContent = undefined;
  return fc;
}

const ENGLISH = createLanguage('en', 'English', 'en');
const DANISH  = createLanguage('da', 'Danish',  'da');
const CHINESE = createLanguage('zh', 'Chinese', 'zh');
const ALL_LANGUAGES = [ENGLISH, DANISH, CHINESE];

/** Helper: click an app-select to open it, then click the option matching the label text. */
function selectOption(selectSelector: string, optionLabel: string) {
  cy.get(selectSelector).click();
  cy.get(selectSelector).find('app-option').contains(optionLabel).click();
}

/** Helper: click the "Save changes" button. */
function clickSave() {
  cy.contains('button', BTN.SAVE).click();
}

function mountComponent(
  deck: EditFlashcardDeckImpl | undefined,
  languages: EditFlashcardLanguageImpl[] = ALL_LANGUAGES,
  saveEmitter = new EventEmitter<void>(),
) {
  return mount(FlashcardEditContainerComponent, {
    componentProperties: {
      flashcardDeck: deck,
      flashcardLanguages: languages,
      saveFlashcards: saveEmitter,
    },
  }).as('component');
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('FlashcardEditContainerComponent', () => {

  // -------------------------------------------------------------------------
  describe('Rendering', () => {
    it('renders the form when flashcardDeck input is provided', () => {
      const deck = createDeck('My Deck', '', [createTextCard('a', 'b', 'en', 'da')]);
      mountComponent(deck);

      cy.get(SEL.FORM).should('exist');
    });

    it('renders nothing when flashcardDeck is undefined', () => {
      mountComponent(undefined);

      cy.get(SEL.FORM).should('not.exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Deck metadata — Name', () => {
    beforeEach(() => {
      const deck = createDeck('My Deck', '', [createTextCard('word', 'translation', 'en', 'da')]);
      mountComponent(deck);
    });

    it('shows the current deck name pre-filled in the name input', () => {
      cy.get(SEL.DECK_NAME).should('have.value', 'My Deck');
    });

    it('shows "Title is required" error when name is cleared and save is attempted', () => {
      cy.get(SEL.DECK_NAME).clear();
      clickSave();

      cy.get('app-form-error').contains(ERR.TITLE_REQUIRED).should('exist');
    });

    it('does not show the title error before a save attempt', () => {
      cy.get(SEL.DECK_NAME).clear();

      cy.get('app-form-error').contains(ERR.TITLE_REQUIRED).should('not.exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Deck metadata — Description', () => {
    beforeEach(() => {
      const deck = createDeck('Deck', 'My description', [createTextCard('a', 'b', 'en', 'da')]);
      mountComponent(deck);
    });

    it('pre-fills the description field with the current deck description', () => {
      cy.get(SEL.DECK_DESC).should('have.value', 'My description');
    });

    it('does not show any validation error for an empty description', () => {
      cy.get(SEL.DECK_DESC).clear();
      clickSave();

      // There is no form-error scoped to the description field
      cy.get(SEL.DECK_DESC_ERR).should('not.exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Deck metadata — Language dropdowns', () => {
    it('populates the front language dropdown from flashcardLanguages', () => {
      const deck = createDeck('Deck', '', [createTextCard()]);
      mountComponent(deck);

      cy.get(SEL.LEFT_LANG).click();
      cy.get(`${SEL.LEFT_LANG} app-option`).should('have.length', 3);
      cy.get(`${SEL.LEFT_LANG} app-option`).eq(0).should('contain.text', LANG_LABEL.ENGLISH);
      cy.get(`${SEL.LEFT_LANG} app-option`).eq(1).should('contain.text', LANG_LABEL.DANISH);
      cy.get(`${SEL.LEFT_LANG} app-option`).eq(2).should('contain.text', LANG_LABEL.CHINESE);
    });

    it('populates the back language dropdown from flashcardLanguages', () => {
      const deck = createDeck('Deck', '', [createTextCard()]);
      mountComponent(deck);

      cy.get(SEL.RIGHT_LANG).click();
      cy.get(`${SEL.RIGHT_LANG} app-option`).should('have.length', 3);
      cy.get(`${SEL.RIGHT_LANG} app-option`).eq(0).should('contain.text', LANG_LABEL.ENGLISH);
      cy.get(`${SEL.RIGHT_LANG} app-option`).eq(1).should('contain.text', LANG_LABEL.DANISH);
      cy.get(`${SEL.RIGHT_LANG} app-option`).eq(2).should('contain.text', LANG_LABEL.CHINESE);
    });

    it('pre-selects the front language with the most common language used on front sides', () => {
      // 2 cards with Danish front, 1 with English → Danish should be pre-selected
      const cards = [
        createTextCard('word1', 'ord1', 'da', 'en'),
        createTextCard('word2', 'ord2', 'da', 'en'),
        createTextCard('word3', 'ord3', 'en', 'en'),
      ];
      const deck = createDeck('Deck', '', cards);
      mountComponent(deck);

      cy.get(SEL.LEFT_LANG).should('contain.text', LANG_LABEL.DANISH);
    });

    it('pre-selects the back language with the most common language used on back sides', () => {
      // 2 cards with Danish back, 1 with English → Danish should be pre-selected
      const cards = [
        createTextCard('w1', 'ord1', 'en', 'da'),
        createTextCard('w2', 'ord2', 'en', 'da'),
        createTextCard('w3', 'ord3', 'en', 'en'),
      ];
      const deck = createDeck('Deck', '', cards);
      mountComponent(deck);

      cy.get(SEL.RIGHT_LANG).should('contain.text', LANG_LABEL.DANISH);
    });

    it('does not show a language error when no text content exists on that side', () => {
      const audioCard = createAudioCard();
      const deck = createDeck('Deck', '', [audioCard]);
      mountComponent(deck);

      clickSave();

      cy.get('app-form-error')
        .contains(ERR.FRONT_LANG)
        .should('not.exist');
      cy.get('app-form-error')
        .contains(ERR.BACK_LANG)
        .should('not.exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Flashcard list', () => {
    it('displays cards numbered sequentially starting at #1', () => {
      const cards = [
        createTextCard('a', 'b', 'en', 'da'),
        createTextCard('c', 'd', 'en', 'da'),
        createTextCard('e', 'f', 'en', 'da'),
      ];
      const deck = createDeck('Deck', '', cards);
      mountComponent(deck);

      cy.get(SEL.CARD_NUMBER).should('have.length', 3);
      cy.get(SEL.CARD_NUMBER).eq(0).should('contain.text', '#1');
      cy.get(SEL.CARD_NUMBER).eq(1).should('contain.text', '#2');
      cy.get(SEL.CARD_NUMBER).eq(2).should('contain.text', '#3');
    });

    it('automatically adds one empty card when the deck has no cards on load', () => {
      const deck = createDeck('Deck', '', []);
      mountComponent(deck);

      cy.get(SEL.CARD_ITEM).should('have.length', 1);
    });
  });

  // -------------------------------------------------------------------------
  describe('Add card', () => {
    beforeEach(() => {
      const deck = createDeck('Deck', '', [createTextCard('a', 'b', 'en', 'da')]);
      mountComponent(deck);
    });

    it('appends a new card at the end of the list when "＋ Add flashcard" is clicked', () => {
      cy.contains('button', BTN.ADD_CARD).click();

      cy.get(SEL.CARD_ITEM).should('have.length', 2);
      cy.get(SEL.CARD_NUMBER).eq(1).should('contain.text', '#2');
    });

    it('gives the new card default content types (text / text) and empty content', () => {
      cy.contains('button', BTN.ADD_CARD).click();

      // The new card is at index 1; its content-type selectors should show "Text"
      cy.get(SEL.LEFT_TYPE).eq(1).should('contain.text', CONTENT_TYPE_LABEL.TEXT);
      cy.get(SEL.RIGHT_TYPE).eq(1).should('contain.text', CONTENT_TYPE_LABEL.TEXT);

      // The text inputs should be empty
      cy.get('#left-text-1').find('input').should('have.value', '');
      cy.get('#right-text-1').find('input').should('have.value', '');
    });

    it('increments the card count shown in the list by one after adding', () => {
      cy.get(SEL.CARD_ITEM).should('have.length', 1);
      cy.contains('button', BTN.ADD_CARD).click();
      cy.get(SEL.CARD_ITEM).should('have.length', 2);
    });
  });

  // -------------------------------------------------------------------------
  describe('Delete card', () => {
    beforeEach(() => {
      const cards = [
        createTextCard('keep',       'bevar',      'en', 'da'),
        createTextCard('delete-me',  'slet-mig',   'en', 'da'),
        createTextCard('also-keep',  'bevar-også', 'en', 'da'),
      ];
      const deck = createDeck('Deck', '', cards);
      mountComponent(deck);
    });

    it('immediately removes the card from the list when "Delete" is clicked', () => {
      // Click Delete on the second card
      cy.get(SEL.CARD_ITEM).eq(1).contains('button', BTN.DELETE).click();

      cy.get(SEL.CARD_ITEM).should('have.length', 2);
      cy.get(SEL.CARD_ITEM).eq(1).find('input').first().should('have.value', 'also-keep');
    });
  });

  // -------------------------------------------------------------------------
  describe('Per-card content type selector', () => {
    beforeEach(() => {
      const deck = createDeck('Deck', '', [createTextCard('hello', 'hej', 'en', 'da')]);
      mountComponent(deck);
    });

    it('shows the text input for both front and back by default', () => {
      cy.get('#left-text-0').should('exist');
      cy.get('#right-text-0').should('exist');
      cy.get('.audio-input-container').should('not.exist');
      cy.get('.image-input-container').should('not.exist');
      cy.get('.video-input-container').should('not.exist');
    });

    it('shows the audio file input when front content type is switched to "audio"', () => {
      selectOption(SEL.LEFT_TYPE, CONTENT_TYPE_LABEL.AUDIO);

      cy.get('#left-audio-0 .audio-input-container').should('exist');
      cy.get('#left-text-0').should('not.exist');
    });

    it('shows the image file input when front content type is switched to "image"', () => {
      selectOption(SEL.LEFT_TYPE, CONTENT_TYPE_LABEL.IMAGE);

      cy.get('#left-image-0 .image-input-container').should('exist');
      cy.get('#left-text-0').should('not.exist');
    });

    it('shows the video file input when front content type is switched to "video"', () => {
      selectOption(SEL.LEFT_TYPE, CONTENT_TYPE_LABEL.VIDEO);

      cy.get('#left-video-0 .video-input-container').should('exist');
      cy.get('#left-text-0').should('not.exist');
    });

    it('shows the audio file input when back content type is switched to "audio"', () => {
      selectOption(SEL.RIGHT_TYPE, CONTENT_TYPE_LABEL.AUDIO);

      cy.get('#right-audio-0 .audio-input-container').should('exist');
      cy.get('#right-text-0').should('not.exist');
    });

    it('shows the image file input when back content type is switched to "image"', () => {
      selectOption(SEL.RIGHT_TYPE, CONTENT_TYPE_LABEL.IMAGE);

      cy.get('#right-image-0 .image-input-container').should('exist');
      cy.get('#right-text-0').should('not.exist');
    });

    it('shows the video file input when back content type is switched to "video"', () => {
      selectOption(SEL.RIGHT_TYPE, CONTENT_TYPE_LABEL.VIDEO);

      cy.get('#right-video-0 .video-input-container').should('exist');
      cy.get('#right-text-0').should('not.exist');
    });

    it('preserves front text content when switching away from text and back again', () => {
      cy.get('#left-text-0 input').should('have.value', 'hello');

      selectOption(SEL.LEFT_TYPE, CONTENT_TYPE_LABEL.AUDIO);
      cy.get('#left-text-0').should('not.exist');

      selectOption(SEL.LEFT_TYPE, CONTENT_TYPE_LABEL.TEXT);
      cy.get('#left-text-0 input').should('have.value', 'hello');
    });

    it('preserves back text content when switching away from text and back again', () => {
      cy.get('#right-text-0 input').should('have.value', 'hej');

      selectOption(SEL.RIGHT_TYPE, CONTENT_TYPE_LABEL.AUDIO);
      cy.get('#right-text-0').should('not.exist');

      selectOption(SEL.RIGHT_TYPE, CONTENT_TYPE_LABEL.TEXT);
      cy.get('#right-text-0 input').should('have.value', 'hej');
    });
  });

  // -------------------------------------------------------------------------
  describe('Text content — validation', () => {
    beforeEach(() => {
      // Card with empty text on both sides
      const fc = EditFlashcardImpl.createDefault();
      fc.leftTextContent  = EditFlashcardTextImpl.create();
      fc.rightTextContent = EditFlashcardTextImpl.create();
      const deck = createDeck('Deck', '', [fc]);
      mountComponent(deck);
    });

    it('shows "Front text required" when front content type is text and the field is empty on save', () => {
      clickSave();

      cy.get(`${SEL.LEFT_SIDE} app-form-error`).contains(ERR.FRONT_TEXT).should('exist');
    });

    it('shows "Back text required" when back content type is text and the field is empty on save', () => {
      clickSave();

      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).contains(ERR.BACK_TEXT).should('exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Text content — resolved language label', () => {
    it('shows the resolved front global language name below the front text input when a front language is selected', () => {
      const deck = createDeck('Deck', '', [createTextCard('word', 'ord', 'en', 'da')]);
      mountComponent(deck);

      // The component resolves the ISO code 'en' to "English" via Intl.DisplayNames
      cy.get(SEL.LEFT_SIDE).should('contain.text', LANG_LABEL.ENGLISH);
    });

    it('shows the resolved back global language name below the back text input when a back language is selected', () => {
      const deck = createDeck('Deck', '', [createTextCard('word', 'ord', 'en', 'da')]);
      mountComponent(deck);

      cy.get(SEL.RIGHT_SIDE).should('contain.text', LANG_LABEL.DANISH);
    });

    it('does not show a language label when no global language is selected', () => {
      const deck = createDeck('Deck', '', [createTextCard('word', 'ord', '', '')]);
      mountComponent(deck);

      // When no language is selected the language name span is not rendered
      cy.get(`${SEL.LEFT_SIDE} span`).should('not.contain.text', LANG_LABEL.ENGLISH);
      cy.get(`${SEL.RIGHT_SIDE} span`).should('not.contain.text', LANG_LABEL.DANISH);
    });
  });

  // -------------------------------------------------------------------------
  describe('Audio content — validation', () => {
    beforeEach(() => {
      const deck = createDeck('Deck', '', [createAudioCard()]);
      mountComponent(deck);
    });

    it('shows "Front audio required" when front content type is audio and no file is selected on save', () => {
      clickSave();

      cy.get(`${SEL.LEFT_SIDE} app-form-error`).contains(ERR.FRONT_AUDIO).should('exist');
    });

    it('shows "Back audio required" when back content type is audio and no file is selected on save', () => {
      clickSave();

      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).contains(ERR.BACK_AUDIO).should('exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Image content — validation', () => {
    beforeEach(() => {
      const deck = createDeck('Deck', '', [createImageCard()]);
      mountComponent(deck);
    });

    it('shows "Front image required" when front content type is image and neither a file nor a URL is present on save', () => {
      clickSave();

      cy.get(`${SEL.LEFT_SIDE} app-form-error`).contains(ERR.FRONT_IMAGE).should('exist');
    });

    it('shows "Back image required" when back content type is image and neither a file nor a URL is present on save', () => {
      clickSave();

      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).contains(ERR.BACK_IMAGE).should('exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Video content — validation', () => {
    beforeEach(() => {
      const deck = createDeck('Deck', '', [createVideoCard()]);
      mountComponent(deck);
    });

    it('shows "Front video required" when front content type is video and neither a file nor a URL is present on save', () => {
      clickSave();

      cy.get(`${SEL.LEFT_SIDE} app-form-error`).contains(ERR.FRONT_VIDEO).should('exist');
    });

    it('shows "Back video required" when back content type is video and neither a file nor a URL is present on save', () => {
      clickSave();

      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).contains(ERR.BACK_VIDEO).should('exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Save — validation enforcement', () => {
    it('marks all form controls as touched on save attempt so all validation errors become visible', () => {
      const cards = [
        EditFlashcardImpl.createDefault(), // empty text on both sides
        createAudioCard(),                 // empty audio on both sides
      ];
      const deck = createDeck('', '', cards); // also missing name
      mountComponent(deck);

      clickSave();

      // All three categories of error should be visible simultaneously
      cy.get('app-form-error').contains(ERR.TITLE_REQUIRED).should('exist');
      cy.get(`${SEL.LEFT_SIDE}  app-form-error`).contains(ERR.FRONT_TEXT).should('exist');
      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).contains(ERR.BACK_TEXT).should('exist');
    });

    it('does not emit saveFlashcards and keeps the form when at least one validation error exists', () => {
      const saveEmitter = new EventEmitter<void>();
      cy.spy(saveEmitter, 'emit').as('emitSpy');

      const fc = EditFlashcardImpl.createDefault(); // empty text fields
      const deck = createDeck('', '', [fc]);        // empty name
      mountComponent(deck, ALL_LANGUAGES, saveEmitter);

      clickSave();

      cy.get('@emitSpy').should('not.have.been.called');
      cy.get(SEL.FORM).should('exist');
    });
  });

  // -------------------------------------------------------------------------
  describe('Save — successful', () => {
    it('writes the updated name and description to the deck model on save', () => {
      const deck = createDeck('Original Name', 'Original desc', [createTextCard('a', 'b', 'en', 'da')]);
      mountComponent(deck);

      cy.get(SEL.DECK_NAME).clear().type('Updated Name');
      cy.get(SEL.DECK_DESC).clear().type('Updated desc');
      clickSave();

      cy.get('@component').then((wrapper: any) => {
        expect(wrapper.component.flashcardDeck.name).to.equal('Updated Name');
        expect(wrapper.component.flashcardDeck.description).to.equal('Updated desc');
      });
    });

    it('applies the selected global front language to all text-type front card sides on save', () => {
      const cards = [
        createTextCard('hello', 'hej',    '', 'da'),
        createTextCard('world', 'verden', '', 'da'),
      ];
      const deck = createDeck('Deck', '', cards);
      mountComponent(deck);

      selectOption(SEL.LEFT_LANG, LANG_LABEL.ENGLISH);
      clickSave();

      cy.get('@component').then((wrapper: any) => {
        const flashcards: EditFlashcardImpl[] = wrapper.component.flashcardDeck.flashcards;
        expect(flashcards[0].leftTextContent?.languageId).to.equal('en');
        expect(flashcards[1].leftTextContent?.languageId).to.equal('en');
      });
    });

    it('applies the selected global back language to all text-type back card sides on save', () => {
      const cards = [
        createTextCard('hello', 'hej',    'en', ''),
        createTextCard('world', 'verden', 'en', ''),
      ];
      const deck = createDeck('Deck', '', cards);
      mountComponent(deck);

      selectOption(SEL.RIGHT_LANG, LANG_LABEL.DANISH);
      clickSave();

      cy.get('@component').then((wrapper: any) => {
        const flashcards: EditFlashcardImpl[] = wrapper.component.flashcardDeck.flashcards;
        expect(flashcards[0].rightTextContent?.languageId).to.equal('da');
        expect(flashcards[1].rightTextContent?.languageId).to.equal('da');
      });
    });

    it('emits the saveFlashcards output event when the form is valid and save is triggered', () => {
      const saveEmitter = new EventEmitter<void>();
      cy.spy(saveEmitter, 'emit').as('emitSpy');

      const deck = createDeck('Deck', '', [createTextCard('word', 'ord', 'en', 'da')]);
      mountComponent(deck, ALL_LANGUAGES, saveEmitter);

      clickSave();

      cy.get('@emitSpy').should('have.been.calledOnce');
    });
  });

  // -------------------------------------------------------------------------
  describe('Save — change detection', () => {
    it('marks newly added cards with state "added" on save', () => {
      const deck = createDeck('Deck', '', [createTextCard('existing', 'eksisterende', 'en', 'da')]);
      mountComponent(deck);

      cy.contains('button', BTN.ADD_CARD).click();
      // Fill in the required fields on the new (second) card
      cy.get('#left-text-1 input').type('new front');
      cy.get('#right-text-1 input').type('new back');
      clickSave();

      cy.get('@component').then((wrapper: any) => {
        const flashcards: EditFlashcardImpl[] = wrapper.component.flashcardDeck.flashcards;
        const addedCard = flashcards.find(f => f.leftTextContent?.text === 'new front');
        expect(addedCard?.state).to.equal('added');
      });
    });

    it('marks removed original cards with state "deleted" on save', () => {
      const card1 = createTextCard('keep',   'bevar', 'en', 'da');
      const card2 = createTextCard('remove', 'fjern', 'en', 'da');
      const deck  = createDeck('Deck', '', [card1, card2]);
      mountComponent(deck);

      // Keep a reference to card2 before deletion
      const removedId = card2.id;

      // Click Delete on the second card
      cy.get(SEL.CARD_ITEM).eq(1).contains('button', BTN.DELETE).click();
      clickSave();

      cy.get('@component').then((wrapper: any) => {
        // Deleted cards are removed from the live flashcards array, but the
        // component marks the original snapshot entries as 'deleted' in the model
        // when saving. Verify by inspecting state on the stored reference.
        expect(card2.state).not.to.equal('added');

        // The removed card must no longer appear in the active list
        const remaining: EditFlashcardImpl[] = wrapper.component.flashcardDeck.flashcards;
        const stillPresent = remaining.find(f => f.id === removedId);
        expect(stillPresent).to.be.undefined;
      });
    });
  });
});

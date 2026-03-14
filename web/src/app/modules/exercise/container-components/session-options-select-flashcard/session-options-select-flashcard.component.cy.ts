import { mount } from 'cypress/angular';
import { SessionOptionsSelectFlashcardComponent } from './session-options-select-flashcard.component';
import { LOCALE_ID } from '@angular/core';
import { ExerciseSessionOptionsSelectFlashcardsDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  FORM: 'form',
  LANGUAGE_SELECT: 'app-select[formControlName="initialFlashcardLanguageId"]',
  HIDE_OPTIONS_TOGGLE: 'app-slide-toogle[formControlName="initiallyHideOptions"]',
  TTS_TOGGLE: 'app-slide-toogle[formControlName="textToSpeechEnabled"]',
  SHUFFLE_TOGGLE: 'app-slide-toogle[formControlName="shuffleFlashcardsEnabled"]',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockOptions(overrides: Partial<ExerciseSessionOptionsSelectFlashcardsDto> = {}): ExerciseSessionOptionsSelectFlashcardsDto {
  return {
    type: 'select-flashcards',
    availableFlashcardLanguages: [
      { id: 'lang1', isoCode: 'en' },
      { id: 'lang2', isoCode: 'es' },
    ],
    initialFlashcardLanguageId: 'lang1',
    initiallyHideOptions: false,
    textToSpeechEnabled: false,
    shuffleFlashcardsEnabled: false,
    ...overrides,
  };
}

function mountComponent(options?: ExerciseSessionOptionsSelectFlashcardsDto) {
  return mount(SessionOptionsSelectFlashcardComponent, {
    componentProperties: { options },
    providers: [
      { provide: LOCALE_ID, useValue: 'en-US' },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('SessionOptionsSelectFlashcardComponent', () => {

  describe('Rendering', () => {
    it('renders the form', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.FORM).should('exist');
    });

    it('renders initial flashcard language select', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.LANGUAGE_SELECT).should('exist');
    });

    it('renders initially hide options toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.HIDE_OPTIONS_TOGGLE).should('exist');
    });

    it('renders text to speech toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.TTS_TOGGLE).should('exist');
    });

    it('renders shuffle flashcards toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.SHUFFLE_TOGGLE).should('exist');
    });
  });

  describe('Form initialization', () => {
    it('initializes with provided initial language', () => {
      const options = createMockOptions({ initialFlashcardLanguageId: 'lang2' });
      mountComponent(options);
      cy.get(SEL.FORM).should('exist');
    });

    it('initializes toggles from options', () => {
      const options = createMockOptions({
        initiallyHideOptions: true,
        textToSpeechEnabled: true,
        shuffleFlashcardsEnabled: true,
      });
      mountComponent(options);
      cy.get(SEL.FORM).should('exist');
    });
  });

  describe('Available languages', () => {
    it('displays available languages in select', () => {
      mountComponent(createMockOptions());
      cy.contains('English').should('exist');
    });
  });

  describe('Form changes', () => {
    it('emits optionsChange when form values change', () => {
      const optionsChangeSpy = cy.spy().as('optionsChangeSpy');
      const options = createMockOptions();
      
      mount(SessionOptionsSelectFlashcardComponent, {
        componentProperties: {
          options,
          optionsChange: { emit: optionsChangeSpy } as any,
        },
        providers: [
          { provide: LOCALE_ID, useValue: 'en-US' },
        ],
      });

      // Toggle text to speech
      cy.get(SEL.TTS_TOGGLE).click();
      
      cy.get('@optionsChangeSpy').should('have.been.called');
    });
  });

  describe('Toggle labels', () => {
    it('displays correct label for initially hide options', () => {
      mountComponent(createMockOptions());
      cy.contains('Initially hide options').should('exist');
    });

    it('displays correct label for text to speech', () => {
      mountComponent(createMockOptions());
      cy.contains('Text to speech').should('exist');
    });

    it('displays correct label for shuffle flashcards', () => {
      mountComponent(createMockOptions());
      cy.contains('Shuffle flashcards').should('exist');
    });
  });
});

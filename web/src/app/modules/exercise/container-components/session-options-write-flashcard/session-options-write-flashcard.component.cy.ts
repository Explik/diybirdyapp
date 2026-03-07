import { mount } from 'cypress/angular';
import { SessionOptionsWriteFlashcardComponent } from './session-options-write-flashcard.component';
import { LOCALE_ID } from '@angular/core';
import { ExerciseSessionOptionsWriteFlashcardsDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  FORM: 'form',
  ANSWER_LANGUAGE_SELECT: 'app-select[formControlName="answerLanguageId"]',
  TTS_TOGGLE: 'app-slide-toogle[formControlName="textToSpeechEnabled"]',
  RETYPE_TOGGLE: 'app-slide-toogle[formControlName="retypeCorrectAnswerEnabled"]',
  SHUFFLE_TOGGLE: 'app-slide-toogle[formControlName="shuffleFlashcardsEnabled"]',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockOptions(overrides: Partial<ExerciseSessionOptionsWriteFlashcardsDto> = {}): ExerciseSessionOptionsWriteFlashcardsDto {
  return {
    type: 'write-flashcards',
    availableAnswerLanguages: [
      { id: 'lang1', isoCode: 'en' },
      { id: 'lang2', isoCode: 'es' },
    ],
    answerLanguageId: 'lang1',
    textToSpeechEnabled: false,
    retypeCorrectAnswerEnabled: false,
    shuffleFlashcardsEnabled: false,
    ...overrides,
  };
}

function mountComponent(options?: ExerciseSessionOptionsWriteFlashcardsDto) {
  return mount(SessionOptionsWriteFlashcardComponent, {
    componentProperties: { options },
    providers: [
      { provide: LOCALE_ID, useValue: 'en-US' },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('SessionOptionsWriteFlashcardComponent', () => {

  describe('Rendering', () => {
    it('renders the form', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.FORM).should('exist');
    });

    it('renders answer language select', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.ANSWER_LANGUAGE_SELECT).should('exist');
    });

    it('renders text to speech toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.TTS_TOGGLE).should('exist');
    });

    it('renders retype correct answer toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.RETYPE_TOGGLE).should('exist');
    });

    it('renders shuffle flashcards toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.SHUFFLE_TOGGLE).should('exist');
    });
  });

  describe('Form initialization', () => {
    it('initializes with provided answer language', () => {
      const options = createMockOptions({ answerLanguageId: 'lang2' });
      mountComponent(options);
      cy.get(SEL.FORM).should('exist');
    });

    it('initializes toggles from options', () => {
      const options = createMockOptions({
        textToSpeechEnabled: true,
        retypeCorrectAnswerEnabled: true,
        shuffleFlashcardsEnabled: true,
      });
      mountComponent(options);
      cy.get(SEL.FORM).should('exist');
    });
  });

  describe('Available languages', () => {
    it('displays available answer languages in select', () => {
      mountComponent(createMockOptions());
      cy.contains('English').should('exist');
    });
  });

  describe('Form changes', () => {
    it('emits optionsChange when form values change', () => {
      const optionsChangeSpy = cy.spy().as('optionsChangeSpy');
      const options = createMockOptions();
      
      mount(SessionOptionsWriteFlashcardComponent, {
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
    it('displays correct label for text to speech', () => {
      mountComponent(createMockOptions());
      cy.contains('Text to speech').should('exist');
    });

    it('displays correct label for retype correct answer', () => {
      mountComponent(createMockOptions());
      cy.contains('Retype correct answer').should('exist');
    });

    it('displays correct label for shuffle flashcards', () => {
      mountComponent(createMockOptions());
      cy.contains('Shuffle flashcards').should('exist');
    });
  });
});

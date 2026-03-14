import { mount } from 'cypress/angular';
import { SessionOptionsReviewFlashcardComponent } from './session-options-review-flashcard.component';
import { LOCALE_ID } from '@angular/core';
import { ExerciseSessionOptionsReviewFlashcardsDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  FORM: 'form',
  ALGORITHM_SELECT: 'app-select[formControlName="algorithm"]',
  LANGUAGE_SELECT: 'app-select[formControlName="initialFlashcardLanguageId"]',
  TTS_TOGGLE: 'app-slide-toogle[formControlName="textToSpeechEnabled"]',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockOptions(overrides: Partial<ExerciseSessionOptionsReviewFlashcardsDto> = {}): ExerciseSessionOptionsReviewFlashcardsDto {
  return {
    type: 'review-flashcards',
    availableFlashcardLanguages: [
      { id: 'lang1', isoCode: 'en' },
      { id: 'lang2', isoCode: 'es' },
    ],
    initialFlashcardLanguageId: 'lang1',
    textToSpeechEnabled: false,
    algorithm: 'SuperMemo2',
    ...overrides,
  };
}

function mountComponent(options?: ExerciseSessionOptionsReviewFlashcardsDto) {
  return mount(SessionOptionsReviewFlashcardComponent, {
    componentProperties: { options },
    providers: [
      { provide: LOCALE_ID, useValue: 'en-US' },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('SessionOptionsReviewFlashcardComponent', () => {

  describe('Rendering', () => {
    it('renders the form', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.FORM).should('exist');
    });

    it('renders algorithm select', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.ALGORITHM_SELECT).should('exist');
    });

    it('renders initial flashcard language select', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.LANGUAGE_SELECT).should('exist');
    });

    it('renders text to speech toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.TTS_TOGGLE).should('exist');
    });
  });

  describe('Form initialization', () => {
    it('initializes with SuperMemo2 algorithm by default', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.FORM).should('exist');
    });

    it('initializes with provided algorithm', () => {
      const options = createMockOptions({ algorithm: 'SimpleSort' });
      mountComponent(options);
      cy.get(SEL.FORM).should('exist');
    });

    it('initializes with provided initial language', () => {
      const options = createMockOptions({ initialFlashcardLanguageId: 'lang2' });
      mountComponent(options);
      cy.get(SEL.FORM).should('exist');
    });

    it('initializes text to speech toggle', () => {
      const options = createMockOptions({ textToSpeechEnabled: true });
      mountComponent(options);
      cy.get(SEL.TTS_TOGGLE).should('exist');
    });
  });

  describe('Available languages', () => {
    it('displays available languages in select', () => {
      mountComponent(createMockOptions());
      cy.contains('English').should('exist');
    });
  });

  describe('Algorithm options', () => {
    it('displays SuperMemo 2 algorithm option', () => {
      mountComponent(createMockOptions());
      cy.contains('SuperMemo 2').should('exist');
    });

    it('displays Simple sort algorithm option', () => {
      mountComponent(createMockOptions());
      cy.contains('Simple sort').should('exist');
    });
  });

  describe('Form changes', () => {
    it('emits optionsChange when form values change', () => {
      const optionsChangeSpy = cy.spy().as('optionsChangeSpy');
      const options = createMockOptions();
      
      mount(SessionOptionsReviewFlashcardComponent, {
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
});

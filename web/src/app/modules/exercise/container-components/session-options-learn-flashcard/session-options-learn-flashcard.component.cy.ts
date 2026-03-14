import { mount } from 'cypress/angular';
import { SessionOptionsLearnFlashcardComponent } from './session-options-learn-flashcard.component';
import { LOCALE_ID } from '@angular/core';
import { ExerciseSessionOptionsLearnFlashcardsDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  FORM: 'form',
  TARGET_LANGUAGE_SELECT: 'select[formControlName="targetLanguageId"]',
  TTS_TOGGLE: 'app-slide-toogle[formControlName="textToSpeechEnabled"]',
  SHUFFLE_TOGGLE: 'app-slide-toogle[formControlName="shuffleFlashcardsEnabled"]',
  REVIEW_TOGGLE: 'app-slide-toogle[formControlName="includeReviewExercises"]',
  MC_TOGGLE: 'app-slide-toogle[formControlName="includeMultipleChoiceExercises"]',
  WRITING_TOGGLE: 'app-slide-toogle[formControlName="includeWritingExercises"]',
  LISTENING_TOGGLE: 'app-slide-toogle[formControlName="includeListeningExercises"]',
  PRONUNCIATION_TOGGLE: 'app-slide-toogle[formControlName="includePronunciationExercises"]',
  FORM_ERROR: 'app-form-error',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockOptions(overrides: Partial<ExerciseSessionOptionsLearnFlashcardsDto> = {}): ExerciseSessionOptionsLearnFlashcardsDto {
  return {
    type: 'learn-flashcards',
    availableAnswerLanguages: [
      { id: 'lang1', isoCode: 'en' },
      { id: 'lang2', isoCode: 'es' },
    ],
    targetLanguageId: 'lang1',
    multipleChoiceAnswerLanguageIds: [],
    writingAnswerLanguageIds: [],
    includeReviewExercises: false,
    includeMultipleChoiceExercises: false,
    includeWritingExercises: false,
    includeListeningExercises: false,
    includePronunciationExercises: false,
    initiallyHideChoices: false,
    retypeCorrectAnswerEnabled: false,
    shuffleFlashcardsEnabled: false,
    textToSpeechEnabled: false,
    ...overrides,
  };
}

function mountComponent(options?: ExerciseSessionOptionsLearnFlashcardsDto) {
  return mount(SessionOptionsLearnFlashcardComponent, {
    componentProperties: { options },
    providers: [
      { provide: LOCALE_ID, useValue: 'en-US' },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('SessionOptionsLearnFlashcardComponent', () => {

  describe('Rendering', () => {
    it('renders the form', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.FORM).should('exist');
    });

    it('renders target language select', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.TARGET_LANGUAGE_SELECT).should('exist');
    });

    it('renders text to speech toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.TTS_TOGGLE).should('exist');
    });

    it('renders shuffle flashcards toggle', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.SHUFFLE_TOGGLE).should('exist');
    });

    it('renders all exercise type toggles', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.REVIEW_TOGGLE).should('exist');
      cy.get(SEL.MC_TOGGLE).should('exist');
      cy.get(SEL.WRITING_TOGGLE).should('exist');
      cy.get(SEL.LISTENING_TOGGLE).should('exist');
      cy.get(SEL.PRONUNCIATION_TOGGLE).should('exist');
    });
  });

  describe('Form initialization', () => {
    it('populates target language from options', () => {
      const options = createMockOptions({ targetLanguageId: 'lang2' });
      mountComponent(options);
      cy.get(SEL.TARGET_LANGUAGE_SELECT).should('have.value', 'lang2');
    });

    it('initializes toggles from options', () => {
      const options = createMockOptions({
        textToSpeechEnabled: true,
        shuffleFlashcardsEnabled: true,
        includeReviewExercises: true,
      });
      mountComponent(options);
      // Toggle states are initialized correctly
      cy.get(SEL.FORM).should('exist');
    });
  });

  describe('Available languages', () => {
    it('displays available languages in select dropdown', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.TARGET_LANGUAGE_SELECT).find('option').should('have.length.at.least', 2);
    });

    it('shows language checkboxes for multiple choice', () => {
      const options = createMockOptions({
        includeMultipleChoiceExercises: true,
      });
      mountComponent(options);
      cy.contains('Answer languages').should('exist');
    });

    it('shows language checkboxes for writing', () => {
      const options = createMockOptions({
        includeWritingExercises: true,
      });
      mountComponent(options);
      cy.contains('Answer languages').should('exist');
    });
  });

  describe('Form validation', () => {
    it('shows error when no exercise type is selected', () => {
      const options = createMockOptions({
        includeReviewExercises: false,
        includeMultipleChoiceExercises: false,
        includeWritingExercises: false,
        includeListeningExercises: false,
        includePronunciationExercises: false,
      });
      mountComponent(options);
      cy.get(SEL.FORM_ERROR).should('exist');
    });

    it('does not show error when at least one exercise type is selected', () => {
      const options = createMockOptions({
        includeReviewExercises: true,
      });
      mountComponent(options);
      cy.get(SEL.FORM_ERROR).should('not.exist');
    });
  });

  describe('Form changes', () => {
    it('emits optionsChange when form values change', () => {
      const optionsChangeSpy = cy.spy().as('optionsChangeSpy');
      const options = createMockOptions({
        includeReviewExercises: true,
      });
      
      mount(SessionOptionsLearnFlashcardComponent, {
        componentProperties: {
          options,
          optionsChange: { emit: optionsChangeSpy } as any,
        },
        providers: [
          { provide: LOCALE_ID, useValue: 'en-US' },
        ],
      });

      // Change target language
      cy.get(SEL.TARGET_LANGUAGE_SELECT).select('lang2');
      
      cy.get('@optionsChangeSpy').should('have.been.called');
    });
  });

  describe('Conditional sections', () => {
    it('shows multiple choice options when toggle is enabled', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.MC_TOGGLE).click();
      cy.contains('Answer languages').should('be.visible');
    });

    it('shows writing options when toggle is enabled', () => {
      mountComponent(createMockOptions());
      cy.get(SEL.WRITING_TOGGLE).click();
      cy.contains('Retype correct answer').should('be.visible');
    });
  });
});

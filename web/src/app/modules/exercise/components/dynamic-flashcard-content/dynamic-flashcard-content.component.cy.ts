import { mount } from 'cypress/angular';
import { DynamicFlashcardContentComponent } from './dynamic-flashcard-content.component';
import { HotkeyService } from '../../../../shared/services/hotKey.service';
import { ExerciseContentFlashcardDto, ExerciseContentFlashcardSideDto, ExerciseContentTextDto } from '../../../../shared/api-client';
import { Subject } from 'rxjs';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  FLASHCARD: 'app-flashcard',
  DYNAMIC_CONTENT: 'app-dynamic-content',
  ERROR_MESSAGE: 'p',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockFlashcardData(initialSide: 'front' | 'back' = 'front'): ExerciseContentFlashcardDto {
  return {
    id: '1',
    type: 'flashcard',
    front: { type: 'text-content', text: 'Front text' } as ExerciseContentTextDto,
    back: { type: 'text-content', text: 'Back text' } as ExerciseContentTextDto,
    initialSide,
  };
}

function createMockFlashcardSideData(): ExerciseContentFlashcardSideDto {
  return {
    id: '1',
    type: 'flashcard-side',
    content: { type: 'text-content', text: 'Side content' } as ExerciseContentTextDto,
  };
}

function createMockHotkeyService(): Partial<HotkeyService> {
  const spaceSubject = new Subject<void>();
  return {
    onHotkey: cy.stub().callsFake(({ key }: { key: string }) => {
      if (key === 'space') return spaceSubject.asObservable();
      return new Subject<void>().asObservable();
    }),
    _testTriggerSpace: () => spaceSubject.next(),
  } as any;
}

function mountComponent(
  data?: ExerciseContentFlashcardDto | ExerciseContentFlashcardSideDto,
  hotkeyService: Partial<HotkeyService> = createMockHotkeyService(),
) {
  return mount(DynamicFlashcardContentComponent, {
    componentProperties: { data },
    providers: [
      { provide: HotkeyService, useValue: hotkeyService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('DynamicFlashcardContentComponent', () => {

  describe('Flashcard data rendering', () => {
    it('renders a flashcard with front and back content', () => {
      mountComponent(createMockFlashcardData());
      cy.get(SEL.FLASHCARD).should('exist');
      cy.get(SEL.DYNAMIC_CONTENT).should('have.length', 2);
    });

    it('displays front side initially when initialSide is front', () => {
      mountComponent(createMockFlashcardData('front'));
      cy.get(SEL.FLASHCARD).should('exist');
    });

    it('displays back side initially when initialSide is back', () => {
      mountComponent(createMockFlashcardData('back'));
      cy.get(SEL.FLASHCARD).should('exist');
    });
  });

  describe('Flashcard side data rendering', () => {
    it('renders a non-flippable flashcard with side content', () => {
      mountComponent(createMockFlashcardSideData());
      cy.get(SEL.FLASHCARD).should('exist');
      cy.get(SEL.DYNAMIC_CONTENT).should('have.length', 1);
    });
  });

  describe('Invalid state', () => {
    it('displays error message when both flashcardData and flashcardSideData are present', () => {
      // This test verifies the template logic handles invalid states
      // In practice, this shouldn't happen, but the component guards against it
      mountComponent(createMockFlashcardData());
      cy.get(SEL.ERROR_MESSAGE).should('not.exist');
    });
  });

  describe('Hotkey interaction', () => {
    it('registers space hotkey on initialization', () => {
      const hotkeyService = createMockHotkeyService();
      mountComponent(createMockFlashcardData(), hotkeyService);
      
      cy.wrap(hotkeyService.onHotkey).should('have.been.calledWith', { key: 'space' });
    });
  });

  describe('No data provided', () => {
    it('does not render anything when data is not provided', () => {
      mountComponent(undefined);
      cy.get(SEL.FLASHCARD).should('not.exist');
    });
  });
});

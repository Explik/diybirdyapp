import { mount } from 'cypress/angular';
import { ExerciseInputSelectOptionsComponent } from './exercise-input-select-options.component';
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { ExerciseInputSelectOptionsDto, SelectOptionsInputFeedbackDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  OPTION: 'app-row-button',
  SHOW_OPTIONS_BUTTON: 'button:contains("Show options")',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockInput(
  options: any[] = [],
  initiallyHideOptions: boolean = false
): ExerciseInputSelectOptionsDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'select-options',
    options,
    initiallyHideOptions,
  };
}

function createMockAudioPlayingService(): Partial<AudioPlayingService> {
  return {
    startPlaying: cy.stub().resolves(),
  };
}

function mountComponent(
  input?: ExerciseInputSelectOptionsDto,
  audioPlayingService: Partial<AudioPlayingService> = createMockAudioPlayingService()
) {
  return mount(ExerciseInputSelectOptionsComponent, {
    componentProperties: { input },
    providers: [
      { provide: AudioPlayingService, useValue: audioPlayingService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputSelectOptionsComponent', () => {

  describe('Rendering options', () => {
    it('renders text options', () => {
      const options = [
        { id: '1', type: 'text', text: 'Option 1' },
        { id: '2', type: 'text', text: 'Option 2' },
      ];
      mountComponent(createMockInput(options));
      cy.contains('Option 1').should('exist');
      cy.contains('Option 2').should('exist');
    });

    it('renders audio options', () => {
      const options = [
        { id: '1', type: 'audio', audioUrl: 'audio1.mp3' },
      ];
      mountComponent(createMockInput(options));
      cy.get(SEL.OPTION).should('have.length', 1);
    });

    it('renders image options', () => {
      const options = [
        { id: '1', type: 'image', imageUrl: 'image1.jpg', text: 'Image 1' },
      ];
      mountComponent(createMockInput(options));
      cy.get(SEL.OPTION).should('have.length', 1);
    });
  });

  describe('Initially hidden options', () => {
    it('shows options immediately when initiallyHideOptions is false', () => {
      const options = [
        { id: '1', type: 'text', text: 'Option 1' },
      ];
      mountComponent(createMockInput(options, false));
      cy.contains('Option 1').should('be.visible');
    });

    it('hides options initially when initiallyHideOptions is true', () => {
      const options = [
        { id: '1', type: 'text', text: 'Option 1' },
      ];
      mountComponent(createMockInput(options, true));
      cy.get(SEL.SHOW_OPTIONS_BUTTON).should('exist');
    });

    it('shows options when Show Options button is clicked', () => {
      const options = [
        { id: '1', type: 'text', text: 'Option 1' },
      ];
      mountComponent(createMockInput(options, true));
      cy.get(SEL.SHOW_OPTIONS_BUTTON).click();
      cy.contains('Option 1').should('be.visible');
    });
  });

  describe('Option selection', () => {
    it('emits optionSelected event when an option is clicked', () => {
      const optionSelectedSpy = cy.spy().as('optionSelectedSpy');
      const options = [
        { id: 'opt1', type: 'text', text: 'Option 1' },
      ];
      
      mount(ExerciseInputSelectOptionsComponent, {
        componentProperties: {
          input: createMockInput(options),
          optionSelected: { emit: optionSelectedSpy } as any,
        },
      });

      cy.get(SEL.OPTION).first().click();
      cy.get('@optionSelectedSpy').should('have.been.calledWith', 'opt1');
    });

    it('does not emit optionSelected when feedback is present', () => {
      const optionSelectedSpy = cy.spy().as('optionSelectedSpy');
      const options = [
        { id: 'opt1', type: 'text', text: 'Option 1' },
      ];
      const inputWithFeedback: ExerciseInputSelectOptionsDto = {
        ...createMockInput(options),
        feedback: { correctOptionIds: ['opt1'], incorrectOptionIds: [] },
      };
      
      mount(ExerciseInputSelectOptionsComponent, {
        componentProperties: {

          input: inputWithFeedback,
          optionSelected: { emit: optionSelectedSpy } as any,
        },
      });

      cy.get(SEL.OPTION).first().click();
      cy.get('@optionSelectedSpy').should('not.have.been.called');
    });
  });
});

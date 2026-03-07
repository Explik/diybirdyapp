import { mount } from 'cypress/angular';
import { DynamicTextContentComponent } from './dynamic-text-content.component';
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { ExerciseContentTextDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  ICON: 'app-icon',
  TEXT: 'span',
  TRANSCRIPTION: '.text-gray-400',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockTextData(
  text: string = 'Test text',
  pronunciationUrl?: string,
  transcription?: string
): ExerciseContentTextDto {
  return {
    id: '1',
    type: 'text-content',
    text,
    pronunciationUrl,
    transcription,
  };
}

function createMockAudioPlayService(
  overrides: Partial<AudioPlayService> = {}
): Partial<AudioPlayService> {
  return {
    toggle: cy.stub().resolves(),
    ...overrides,
  };
}

function mountComponent(
  data?: ExerciseContentTextDto,
  audioPlayService: Partial<AudioPlayService> = createMockAudioPlayService(),
) {
  return mount(DynamicTextContentComponent, {
    componentProperties: { data },
    providers: [
      { provide: AudioPlayService, useValue: audioPlayService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('DynamicTextContentComponent', () => {

  describe('Rendering', () => {
    it('renders the text when data is provided', () => {
      const testText = 'Hello World';
      mountComponent(createMockTextData(testText));
      cy.contains(testText).should('exist');
    });

    it('renders the audio icon when pronunciationUrl is provided', () => {
      mountComponent(createMockTextData('Test', 'audio.mp3'));
      cy.get(SEL.ICON).should('exist');
    });

    it('does not render the audio icon when pronunciationUrl is not provided', () => {
      mountComponent(createMockTextData('Test'));
      cy.get(SEL.ICON).should('not.exist');
    });

    it('renders the transcription when provided', () => {
      const testTranscription = 'test transcription';
      mountComponent(createMockTextData('Test', undefined, testTranscription));
      cy.get(SEL.TRANSCRIPTION).should('contain.text', testTranscription);
    });

    it('does not render transcription when not provided', () => {
      mountComponent(createMockTextData('Test'));
      cy.get(SEL.TRANSCRIPTION).should('not.exist');
    });

    it('does not render anything when data is not provided', () => {
      mountComponent(undefined);
      cy.get(SEL.TEXT).should('not.exist');
    });
  });

  describe('Audio playback', () => {
    it('calls audioPlayService.toggle when clicked with pronunciationUrl', () => {
      const audioPlayService = createMockAudioPlayService();
      const testUrl = 'audio.mp3';
      mountComponent(createMockTextData('Test', testUrl), audioPlayService);

      cy.get(SEL.ICON).click();
      
      cy.wrap(audioPlayService.toggle).should('have.been.calledWith', testUrl);
    });

    it('calls audioPlayService.toggle when Enter key is pressed with pronunciationUrl', () => {
      const audioPlayService = createMockAudioPlayService();
      const testUrl = 'audio.mp3';
      mountComponent(createMockTextData('Test', testUrl), audioPlayService);

      cy.get(SEL.ICON).type('{enter}');
      
      cy.wrap(audioPlayService.toggle).should('have.been.calledWith', testUrl);
    });

    it('does not call audioPlayService when clicked without pronunciationUrl', () => {
      const audioPlayService = createMockAudioPlayService();
      mountComponent(createMockTextData('Test'), audioPlayService);

      cy.contains('Test').click();
      
      cy.wrap(audioPlayService.toggle).should('not.have.been.called');
    });
  });
});

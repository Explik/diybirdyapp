import { mount } from 'cypress/angular';
import { DynamicAudioContentComponent } from './dynamic-audio-content.component';
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { ExerciseContentAudioDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  ICON: 'app-icon',
  WAVEFORM: 'svg',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockAudioData(audioUrl: string = 'test-audio.mp3'): ExerciseContentAudioDto {
  return {
    id: '1',
    type: 'audio-content',
    audioUrl,
  };
}

function createMockAudioPlayService(
  overrides: Partial<AudioPlayService> = {}
): Partial<AudioPlayService> {
  return {
    play: cy.stub().resolves(),
    stop: cy.stub().resolves(),
    toggle: cy.stub().resolves(),
    ...overrides,
  };
}

function mountComponent(
  data?: ExerciseContentAudioDto,
  audioPlayService: Partial<AudioPlayService> = createMockAudioPlayService(),
  autoplay = true,
) {
  return mount(DynamicAudioContentComponent, {
    componentProperties: { data, autoplay },
    providers: [
      { provide: AudioPlayService, useValue: audioPlayService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('DynamicAudioContentComponent', () => {

  describe('Rendering', () => {
    it('renders the icon when data is provided', () => {
      mountComponent(createMockAudioData());
      cy.get(SEL.ICON).should('exist');
    });

    it('renders the waveform SVG when data is provided', () => {
      mountComponent(createMockAudioData());
      cy.get(SEL.WAVEFORM).should('exist');
    });

    it('does not render anything when data is not provided', () => {
      mountComponent(undefined);
      cy.get(SEL.ICON).should('not.exist');
      cy.get(SEL.WAVEFORM).should('not.exist');
    });
  });

  describe('Audio playback', () => {
    it('calls audioPlayService.play on appearance when autoplay is enabled', () => {
      const audioPlayService = createMockAudioPlayService();
      const testUrl = 'test-audio.mp3';
      mountComponent(createMockAudioData(testUrl), audioPlayService);

      cy.wrap(audioPlayService.play).should('have.been.calledWith', testUrl);
    });

    it('does not call audioPlayService.play on appearance when autoplay is disabled', () => {
      const audioPlayService = createMockAudioPlayService();
      mountComponent(createMockAudioData('test-audio.mp3'), audioPlayService, false);

      cy.wrap(audioPlayService.play).should('not.have.been.called');
    });

    it('calls audioPlayService.toggle when clicked', () => {
      const audioPlayService = createMockAudioPlayService();
      const testUrl = 'test-audio.mp3';
      mountComponent(createMockAudioData(testUrl), audioPlayService);

      cy.get(SEL.ICON).click();
      
      cy.wrap(audioPlayService.toggle).should('have.been.calledWith', testUrl);
    });

    it('calls audioPlayService.toggle when Enter key is pressed', () => {
      const audioPlayService = createMockAudioPlayService();
      const testUrl = 'test-audio.mp3';
      mountComponent(createMockAudioData(testUrl), audioPlayService);

      cy.get(SEL.ICON).type('{enter}');
      
      cy.wrap(audioPlayService.toggle).should('have.been.calledWith', testUrl);
    });
  });
});

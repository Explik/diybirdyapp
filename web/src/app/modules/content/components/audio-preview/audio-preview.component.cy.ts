import { mount } from 'cypress/angular';
import { AudioPreviewComponent } from './audio-preview.component';
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { EditFlashcardAudio } from '../../models/editFlashcard.model';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER: '.audio-preview-container',
  ICON:      'app-icon span',
  WAVEFORM:  'svg',
} as const;

const ICON_ATTR = {
  IDLE:    'mdi-volume-low',
  PLAYING: 'mdi-volume-high',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockAudio(): EditFlashcardAudio {
  return {
    generateElement: () => {
      const el = document.createElement('audio');
      el.play = cy.stub().resolves() as any;
      return el;
    },
    getChanges: () => undefined,
  };
}

function createMockPlayingService(
  overrides: Partial<AudioPlayingService> = {}
): Partial<AudioPlayingService> {
  return {
    startPlayingEditFlashcard: cy.stub().resolves(),
    stopPlaying: cy.stub(),
    ...overrides,
  };
}

function mountComponent(
  data: EditFlashcardAudio = createMockAudio(),
  playService: Partial<AudioPlayingService> = createMockPlayingService(),
) {
  return mount(AudioPreviewComponent, {
    componentProperties: { data },
    providers: [
      { provide: AudioPlayingService, useValue: playService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('AudioPreviewComponent', () => {

  describe('Rendering', () => {
    it('renders the audio preview container', () => {
      mountComponent();
      cy.get(SEL.CONTAINER).should('exist');
    });

    it('renders the waveform SVG graphic', () => {
      mountComponent();
      cy.get(SEL.WAVEFORM).should('exist');
    });

    it('shows the volume-low icon when not playing', () => {
      mountComponent();
      assertAudioIcon(ICON_ATTR.IDLE);
    });
  });

  describe('Playback interaction', () => {
    it('calls startPlayingEditFlashcard when clicked while idle', () => {
      const playService = createMockPlayingService();
      mountComponent(createMockAudio(), playService);

      cy.get(SEL.CONTAINER).click();
      
      cy.wrap(playService.startPlayingEditFlashcard).should('have.been.called');
    });

    it('shows the volume-high icon while playing after click', () => {
      const playService = createMockPlayingService({
        startPlayingEditFlashcard: cy.stub().returns(new Promise(() => {})),
      });
      mountComponent(createMockAudio(), playService);

      cy.get(SEL.CONTAINER).click();

      assertAudioIcon(ICON_ATTR.PLAYING);
    });

    it('calls stopPlaying when clicked while already playing', () => {
      const playService = createMockPlayingService({
        startPlayingEditFlashcard: cy.stub().returns(new Promise(() => {})),
      });
      mountComponent(createMockAudio(), playService);

      cy.get(SEL.CONTAINER).click();   // start playing
      cy.get(SEL.CONTAINER).click();   // stop playing

      cy.wrap(playService.stopPlaying).should('have.been.called');
    });

    it('shows the volume-low icon again after stopping', () => {
      const playService = createMockPlayingService({
        startPlayingEditFlashcard: cy.stub().returns(new Promise(() => {})),
      });
      mountComponent(createMockAudio(), playService);

      cy.get(SEL.CONTAINER).click();   // start playing
      cy.get(SEL.CONTAINER).click();   // stop playing

      assertAudioIcon(ICON_ATTR.IDLE);
    });
  });
});

function assertAudioIcon(expectedIcon: string) {
  cy.get(SEL.ICON).should('have.class', expectedIcon);
}
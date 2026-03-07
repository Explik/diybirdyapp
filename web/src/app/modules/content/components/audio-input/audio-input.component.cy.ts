import { mount } from 'cypress/angular';
import { AudioInputComponent } from './audio-input.component';
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { AudioRecordingService } from '../../services/audioRecording.service';
import { FileAudioContent } from '../../models/editFlashcard.model';
import { BehaviorSubject } from 'rxjs';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER:  '.audio-input-container',
  RECORD_BTN: 'button',
  UPLOAD_LBL: 'label',
  FILE_INPUT:  'input[type="file"]',
  SAVE_BTN:   'button',
  CANCEL_BTN: 'button',
  PLAY_BTN:   'button',
  CLEAR_BTN:  'button',
  FILENAME:   'p',
} as const;

const ICON = {
  UPLOAD:    '.mdi-cloud-arrow-up',
  DROPPING:  '.mdi-cloud-arrow-down',
  RECORDING: '.mdi-microphone',
  PREVIEW:   '.mdi-volume-high',
} as const;

const BTN = {
  RECORD: 'Record',
  UPLOAD: 'Upload',
  SAVE:   'Save',
  CANCEL: 'Cancel',
  PLAY:   'Play',
  PAUSE:  'Pause',
  CLEAR:  'Clear',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockPlayingService() {
  const state$ = new BehaviorSubject<'playing' | 'paused' | 'stopped'>('stopped');
  return {
    getCurrentState: () => state$.asObservable(),
    startPlayingEditFlashcard: cy.stub().resolves(),
    stopPlaying: cy.stub(),
    state$,
  };
}

function createMockRecordingService() {
  return {
    startRecording: cy.stub().returns(new Promise(() => {})),
    stopRecording:  cy.stub(),
    cancelRecording: cy.stub(),
  };
}

function mountComponent(audioData?: any, playService = createMockPlayingService(), recordingService = createMockRecordingService()) {
  return mount(AudioInputComponent, {
    componentProperties: {
      audioData,
    },
    providers: [
      { provide: AudioPlayingService,  useValue: playService },
      { provide: AudioRecordingService, useValue: recordingService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('AudioInputComponent', () => {

  describe('Empty state', () => {
    beforeEach(() => { mountComponent(undefined); });

    it('shows the upload cloud icon when no audio is loaded', () => {
      cy.get(ICON.UPLOAD).should('exist');
    });

    it('shows the "Record" button in empty state', () => {
      cy.contains('button', BTN.RECORD).should('exist');
    });

    it('shows the "Upload" label in empty state', () => {
      cy.contains('label', BTN.UPLOAD).should('exist');
    });
  });

  describe('Recording state', () => {
    it('shows the microphone icon while recording', () => {
      const recordingService = createMockRecordingService();
      recordingService.startRecording = cy.stub().returns(new Promise(() => {}));
      mountComponent(undefined, createMockPlayingService(), recordingService);

      cy.contains('button', BTN.RECORD).click();
      cy.get(ICON.RECORDING).should('exist');
    });

    it('shows Save and Cancel buttons while recording', () => {
      const recordingService = createMockRecordingService();
      recordingService.startRecording = cy.stub().returns(new Promise(() => {}));
      mountComponent(undefined, createMockPlayingService(), recordingService);

      cy.contains('button', BTN.RECORD).click();
      cy.contains('button', BTN.SAVE).should('exist');
      cy.contains('button', BTN.CANCEL).should('exist');
    });

    it('returns to empty state when Cancel is clicked during recording', () => {
      const recordingService = createMockRecordingService();
      recordingService.startRecording = cy.stub().returns(new Promise(() => {}));
      mountComponent(undefined, createMockPlayingService(), recordingService);

      cy.contains('button', BTN.RECORD).click();
      cy.contains('button', BTN.CANCEL).click();
      cy.get(ICON.UPLOAD).should('exist');
    });
  });

  describe('Preview state', () => {
    beforeEach(() => {
      const fakeFile = new File(['audio'], 'test-audio.mp3', { type: 'audio/mp3' });
      const audioData = new FileAudioContent(fakeFile);
      mountComponent(audioData);
    });

    it('shows the volume-high icon when audio is loaded', () => {
      cy.get(ICON.PREVIEW).should('exist');
    });

    it('shows the audio filename in preview state', () => {
      cy.get(SEL.FILENAME).should('contain.text', 'test-audio.mp3');
    });

    it('shows the "Play" button in preview state when not playing', () => {
      cy.contains('button', BTN.PLAY).should('exist');
    });

    it('shows the "Clear" button in preview state', () => {
      cy.contains('button', BTN.CLEAR).should('exist');
    });

    it('returns to empty state when "Clear" is clicked', () => {
      cy.contains('button', BTN.CLEAR).click();
      cy.get(ICON.UPLOAD).should('exist');
    });
  });

  describe('Dropping state', () => {
    it('shows the dropping cloud icon during drag-over', () => {
      mountComponent(undefined);

      cy.get(SEL.CONTAINER)
        .trigger('dragenter', { dataTransfer: new DataTransfer() });

      cy.get(ICON.DROPPING).should('exist');
    });

    it('shows "Drop file here!" text during drag-over', () => {
      mountComponent(undefined);

      cy.get(SEL.CONTAINER)
        .trigger('dragenter', { dataTransfer: new DataTransfer() });

      cy.get(SEL.CONTAINER).should('contain.text', 'Drop file here!');
    });
  });

  describe('Filename truncation', () => {
    it('truncates a long filename keeping the extension visible', () => {
      const longName = 'a-very-long-audio-filename-that-exceeds-limit.mp3';
      const fakeFile  = new File(['audio'], longName, { type: 'audio/mp3' });
      mountComponent(new FileAudioContent(fakeFile));

      cy.get(SEL.FILENAME).invoke('text').then(text => {
        expect(text.trim().length).to.be.lessThan(longName.length);
        expect(text.trim()).to.match(/\.mp3$/);
      });
    });
  });
});

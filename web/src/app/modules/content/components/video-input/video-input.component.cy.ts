import { mount } from 'cypress/angular';
import { VideoInputComponent } from './video-input.component';
import { EditFlashcardVideoImpl } from '../../models/editFlashcard.model';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER:   '.video-input-container',
  FILE_INPUT:   'input[type="file"]',
  VIDEO_PREVIEW: 'video',
  FILENAME:     'p',
} as const;

const ICON = {
  UPLOAD:   '.mdi-cloud-arrow-up',
  DROPPING: '.mdi-cloud-arrow-down',
} as const;

const BTN = {
  RECORD: 'Record',
  UPLOAD: 'Upload',
  PLAY:   'Play',
  PAUSE:  'Pause',
  CLEAR:  'Clear',
} as const;

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('VideoInputComponent', () => {

  describe('Empty state', () => {
    beforeEach(() => { mount(VideoInputComponent); });

    it('shows the upload cloud icon in empty state', () => {
      cy.get(ICON.UPLOAD).should('exist');
    });

    it('shows the "Record" button in empty state', () => {
      cy.contains('button', BTN.RECORD).should('exist');
    });

    it('shows the "Upload" label in empty state', () => {
      cy.contains('label', BTN.UPLOAD).should('exist');
    });

    it('shows a hidden file input accepting video files', () => {
      cy.get('input[type="file"][accept="video/*"]').should('exist');
    });
  });

  describe('Preview state', () => {
    beforeEach(() => {
      const videoUrl = 'https://example.com/test-video.mp4';
      const videoData = EditFlashcardVideoImpl.createFromDto({ id: 'v1', videoUrl, type: 'video' } as any);
      mount(VideoInputComponent, {
        componentProperties: { videoData },
      });
    });

    it('shows the video element in preview state', () => {
      cy.get(SEL.VIDEO_PREVIEW).should('exist');
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
      mount(VideoInputComponent);

      cy.get(SEL.CONTAINER)
        .trigger('dragenter', { dataTransfer: new DataTransfer() });

      cy.get(ICON.DROPPING).should('exist');
    });

    it('shows "Drop file here!" text during drag-over', () => {
      mount(VideoInputComponent);

      cy.get(SEL.CONTAINER)
        .trigger('dragenter', { dataTransfer: new DataTransfer() });

      cy.get(SEL.CONTAINER).should('contain.text', 'Drop file here!');
    });
  });

  describe('Filename truncation', () => {
    it('truncates a long video filename and keeps the extension visible', () => {
      const longName = 'a-very-long-video-filename-that-exceeds-the-limit.mp4';
      const fakeFile  = new File(['video'], longName, { type: 'video/mp4' });
      const videoData = EditFlashcardVideoImpl.createFromFile(fakeFile);

      mount(VideoInputComponent, { componentProperties: { videoData } });

      cy.get(SEL.FILENAME).invoke('text').then(text => {
        expect(text.trim().length).to.be.lessThan(longName.length);
        expect(text.trim()).to.match(/\.mp4$/);
      });
    });
  });
});

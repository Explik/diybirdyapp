import { mount } from 'cypress/angular';
import { ImageInputComponent } from './image-input.component';
import { EditFlashcardImageImpl } from '../../models/editFlashcard.model';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER:   '.image-input-container',
  FILE_INPUT:   'input[type="file"]',
  PREVIEW_IMG:  'img',
  FILENAME:     'p',
} as const;

const ICON = {
  UPLOAD:   '.mdi-cloud-arrow-up',
  DROPPING: '.mdi-cloud-arrow-down',
} as const;

const BTN = {
  CAPTURE: 'Capture',
  UPLOAD:  'Upload',
  CLEAR:   'Clear',
} as const;

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ImageInputComponent', () => {

  describe('Empty state', () => {
    beforeEach(() => { mount(ImageInputComponent); });

    it('shows the upload cloud icon in empty state', () => {
      cy.get(ICON.UPLOAD).should('exist');
    });

    it('shows the "Capture" button in empty state', () => {
      cy.contains('button', BTN.CAPTURE).should('exist');
    });

    it('shows the "Upload" label in empty state', () => {
      cy.contains('label', BTN.UPLOAD).should('exist');
    });

    it('shows a hidden file input accepting image files', () => {
      cy.get('input[type="file"][accept="image/*"]').should('exist');
    });
  });

  describe('Preview state', () => {
    beforeEach(() => {
      const imageUrl = 'https://example.com/image.png';
      const imageData = EditFlashcardImageImpl.createFromUrl(imageUrl);
      mount(ImageInputComponent, {
        componentProperties: { imageData },
      });
    });

    it('shows a preview image in preview state', () => {
      cy.get(SEL.PREVIEW_IMG).should('exist');
    });

    it('shows the image src matching the provided URL', () => {
      cy.get(SEL.PREVIEW_IMG).should('have.attr', 'src', 'https://example.com/image.png');
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
      mount(ImageInputComponent);

      cy.get(SEL.CONTAINER)
        .trigger('dragenter', { dataTransfer: new DataTransfer() });

      cy.get(ICON.DROPPING).should('exist');
    });
  });

  describe('Filename truncation', () => {
    it('truncates a long image filename and keeps the extension visible', () => {
      const longName  = 'a-very-long-image-filename-that-exceeds-the-limit.png';
      const fakeFile  = new File(['img'], longName, { type: 'image/png' });
      const imageData = EditFlashcardImageImpl.createFromFile(fakeFile);

      mount(ImageInputComponent, { componentProperties: { imageData } });

      cy.get(SEL.FILENAME).first().invoke('text').then(text => {
        expect(text.trim().length).to.be.lessThan(longName.length);
        expect(text.trim()).to.match(/\.png$/);
      });
    });
  });
});

import { mount } from 'cypress/angular';
import { ImagePreviewComponent } from './image-preview.component';
import { EditFlashcardImageImpl } from '../../models/editFlashcard.model';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  IMG: 'img.image-preview-container',
} as const;

const IMAGE_URL = 'https://example.com/test-image.png';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function mountComponent(src: string = IMAGE_URL) {
  const data = EditFlashcardImageImpl.createFromUrl(src);
  return mount(ImagePreviewComponent, {
    componentProperties: { data },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ImagePreviewComponent', () => {

  describe('Rendering', () => {
    it('renders an img element', () => {
      mountComponent();
      cy.get(SEL.IMG).should('exist');
    });

    it('sets the src attribute to the value returned by data.getSrc()', () => {
      mountComponent(IMAGE_URL);
      cy.get(SEL.IMG).should('have.attr', 'src', IMAGE_URL);
    });

    it('updates the src when different image data is provided', () => {
      const other = 'https://example.com/other-image.jpg';
      mountComponent(other);
      cy.get(SEL.IMG).should('have.attr', 'src', other);
    });
  });
});

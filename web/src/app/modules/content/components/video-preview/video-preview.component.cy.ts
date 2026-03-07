import { mount } from 'cypress/angular';
import { VideoPreviewComponent } from './video-preview.component';
import { EditFlashcardVideoImpl } from '../../models/editFlashcard.model';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  VIDEO: 'video.video-preview-container',
} as const;

const VIDEO_URL = 'https://example.com/test-video.mp4';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function mountComponent(src: string = VIDEO_URL) {
  const data = EditFlashcardVideoImpl.createFromDto({ id: 'v1', videoUrl: src, type: 'video' } as any);
  return mount(VideoPreviewComponent, {
    componentProperties: { data },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('VideoPreviewComponent', () => {

  describe('Rendering', () => {
    it('renders a video element', () => {
      mountComponent();
      cy.get(SEL.VIDEO).should('exist');
    });

    it('sets the src attribute to the value returned by data.getSrc()', () => {
      mountComponent(VIDEO_URL);
      cy.get(SEL.VIDEO).should('have.attr', 'src', VIDEO_URL);
    });

    it('renders the video element with controls', () => {
      mountComponent();
      cy.get(SEL.VIDEO).should('have.attr', 'controls');
    });

    it('updates the src when different video data is provided', () => {
      const other = 'https://example.com/other-video.webm';
      mountComponent(other);
      cy.get(SEL.VIDEO).should('have.attr', 'src', other);
    });
  });
});

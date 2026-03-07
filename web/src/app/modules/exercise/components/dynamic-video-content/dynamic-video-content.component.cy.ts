import { mount } from 'cypress/angular';
import { DynamicVideoContentComponent } from './dynamic-video-content.component';
import { ExerciseContentVideoDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  VIDEO: 'video',
  SOURCE: 'source',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockVideoData(videoUrl: string = 'test-video.mp4'): ExerciseContentVideoDto {
  return {
    id: '1',
    type: 'video-content',
    videoUrl,
  };
}

function mountComponent(data?: ExerciseContentVideoDto) {
  return mount(DynamicVideoContentComponent, {
    componentProperties: { data },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('DynamicVideoContentComponent', () => {

  describe('Rendering', () => {
    it('renders the video element when data is provided', () => {
      const testUrl = 'test-video.mp4';
      mountComponent(createMockVideoData(testUrl));
      cy.get(SEL.VIDEO).should('exist');
      cy.get(SEL.VIDEO).should('have.attr', 'controls');
    });

    it('renders the video source with correct URL', () => {
      const testUrl = 'test-video.mp4';
      mountComponent(createMockVideoData(testUrl));
      cy.get(SEL.SOURCE).should('have.attr', 'src', testUrl);
      cy.get(SEL.SOURCE).should('have.attr', 'type', 'video/mp4');
    });

    it('does not render the video when data is not provided', () => {
      mountComponent(undefined);
      cy.get(SEL.VIDEO).should('not.exist');
    });
  });
});

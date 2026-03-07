import { mount } from 'cypress/angular';
import { DynamicContentComponent } from './dynamic-content.component';
import { ExerciseContentAudioDto, ExerciseContentDto, ExerciseContentImageDto, ExerciseContentTextDto, ExerciseContentVideoDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  AUDIO_CONTENT: 'app-dynamic-audio-content',
  IMAGE_CONTENT: 'app-dynamic-image-content',
  TEXT_CONTENT: 'app-dynamic-text-content',
  VIDEO_CONTENT: 'app-dynamic-video-content',
  UNKNOWN_MESSAGE: 'p',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function mountComponent(data?: ExerciseContentDto) {
  return mount(DynamicContentComponent, {
    componentProperties: { data },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('DynamicContentComponent', () => {

  describe('Audio content rendering', () => {
    it('renders audio content component when type is audio-content', () => {
      const audioData: ExerciseContentAudioDto = {
        id: '1',
        type: 'audio-content',
        audioUrl: 'test.mp3',
      };
      mountComponent(audioData);
      cy.get(SEL.AUDIO_CONTENT).should('exist');
    });
  });

  describe('Image content rendering', () => {
    it('renders image content component when type is image-content', () => {
      const imageData: ExerciseContentImageDto = {
        id: '1',
        type: 'image-content',
        imageUrl: 'test.jpg',
      };
      mountComponent(imageData);
      cy.get(SEL.IMAGE_CONTENT).should('exist');
    });
  });

  describe('Text content rendering', () => {
    it('renders text content component when type is text-content', () => {
      const textData: ExerciseContentTextDto = {
        id: '1',
        type: 'text-content',
        text: 'Hello',
      };
      mountComponent(textData);
      cy.get(SEL.TEXT_CONTENT).should('exist');
    });
  });

  describe('Video content rendering', () => {
    it('renders video content component when type is video-content', () => {
      const videoData: ExerciseContentVideoDto = {
        id: '1',
        type: 'video-content',
        videoUrl: 'test.mp4',
      };
      mountComponent(videoData);
      cy.get(SEL.VIDEO_CONTENT).should('exist');
    });
  });

  describe('Unknown content type', () => {
    it('displays unknown content type message for unsupported types', () => {
      const unknownData: ExerciseContentDto = {
        id: '1',
        type: 'unknown-type' as any,
      };
      mountComponent(unknownData);
      cy.get(SEL.UNKNOWN_MESSAGE).should('contain.text', 'Unknown content type');
    });
  });

  describe('No data provided', () => {
    it('does not render anything when data is not provided', () => {
      mountComponent(undefined);
      cy.get(SEL.AUDIO_CONTENT).should('not.exist');
      cy.get(SEL.IMAGE_CONTENT).should('not.exist');
      cy.get(SEL.TEXT_CONTENT).should('not.exist');
      cy.get(SEL.VIDEO_CONTENT).should('not.exist');
    });
  });
});

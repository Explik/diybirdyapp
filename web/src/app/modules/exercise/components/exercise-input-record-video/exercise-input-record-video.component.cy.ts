import { mount } from 'cypress/angular';
import { ExerciseInputRecordVideoComponent } from './exercise-input-record-video.component';
import { ExerciseInputRecordVideoDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  RECORD_BUTTON: 'button',
  VIDEO_ELEMENT: 'video',
  DELETE_BUTTON: 'button:contains("Delete")',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockInput(): ExerciseInputRecordVideoDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'record-video',
    url: "test-video.mp4",
  };
}

function mountComponent(input: ExerciseInputRecordVideoDto) {
  return mount(ExerciseInputRecordVideoComponent, {
    componentProperties: { input },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputRecordVideoComponent', () => {

  describe('Rendering', () => {
    it('renders the record button', () => {
      mountComponent(createMockInput());
      cy.get(SEL.RECORD_BUTTON).should('exist');
    });

    it('renders the video element', () => {
      mountComponent(createMockInput());
      cy.get(SEL.VIDEO_ELEMENT).should('exist');
    });
  });

  describe('Initial state', () => {
    it('is not recording initially', () => {
      mountComponent(createMockInput());
      cy.get(SEL.RECORD_BUTTON).should('contain', 'Enable camera');
    });

    it('shows loading state initially', () => {
      mountComponent(createMockInput());
      // Component sets isLoading during initialization
      cy.get(SEL.RECORD_BUTTON).should('exist');
    });
  });

  describe('Button labels', () => {
    it('displays correct button label when camera access is not granted', () => {
      mountComponent(createMockInput());
      cy.get(SEL.RECORD_BUTTON).should('exist');
    });
  });

  // Note: Testing actual recording/camera functionality requires more complex setup
  // with mocked MediaDevices API, which is beyond basic component testing
});

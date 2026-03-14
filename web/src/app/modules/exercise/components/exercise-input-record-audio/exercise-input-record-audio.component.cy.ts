import { mount } from 'cypress/angular';
import { ExerciseInputRecordAudioComponent } from './exercise-input-record-audio.component';
import { AudioUploadService } from '../../../../shared/services/audioUpload.service';
import { ExerciseInputRecordAudioDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  RECORD_BUTTON: 'button',
  ICON: 'app-icon',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockInput(): ExerciseInputRecordAudioDto {
  return {
    id: '1',
    sessionId: 'session1',
    type: 'record-audio',
    url: "test-audio.mp3",
  };
}

function createMockAudioUploadService(): Partial<AudioUploadService> {
  return {
    // Add any necessary mock methods
  };
}

function mountComponent(
  input?: ExerciseInputRecordAudioDto,
  audioUploadService: Partial<AudioUploadService> = createMockAudioUploadService()
) {
  return mount(ExerciseInputRecordAudioComponent, {
    componentProperties: { input },
    providers: [
      { provide: AudioUploadService, useValue: audioUploadService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseInputRecordAudioComponent', () => {

  describe('Rendering', () => {
    it('renders the record button', () => {
      mountComponent(createMockInput());
      cy.get(SEL.RECORD_BUTTON).should('exist');
    });

    it('renders the icon', () => {
      mountComponent(createMockInput());
      cy.get(SEL.ICON).should('exist');
    });
  });

  describe('Initial state', () => {
    it('is not recording initially', () => {
      mountComponent(createMockInput());
      cy.get(SEL.RECORD_BUTTON).should('not.have.class', 'recording');
    });
  });

  describe('Feedback display', () => {
    it('displays correct feedback values', () => {
      const input: ExerciseInputRecordAudioDto = {
          type: 'record-audio',
          url: "test-audio.mp3",
          feedback: {
              correctValues: ['Correct pronunciation'],
              incorrectValues: [],
          },
          sessionId: ''
      };
      mountComponent(input);
      cy.contains('Correct pronunciation').should('exist');
    });

    it('displays incorrect feedback values', () => {
      const input: ExerciseInputRecordAudioDto = {
          type: 'record-audio',
          url: "test-audio.mp3",
          feedback: {
              correctValues: [],
              incorrectValues: ['Incorrect pronunciation'],
          },
          sessionId: ''
      };
      mountComponent(input);
      cy.contains('Incorrect pronunciation').should('exist');
    });
  });

  // Note: Testing actual recording functionality requires more complex setup
  // with mocked MediaRecorder API, which is beyond basic component testing
});

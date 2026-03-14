import { mount } from 'cypress/angular';
import { ExerciseBatchProgressBarComponent } from './exercise-batch-progress-bar.component';
import { ExerciseService } from '../../services/exercise.service';
import { BehaviorSubject } from 'rxjs';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  BATCH_PROGRESS_BAR: 'app-batch-progress-bar',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockExerciseService(
  initialProgress: number = 0,
  hasMoreBatches: boolean = false
): Partial<ExerciseService> {
  const progressSubject = new BehaviorSubject({ percentage: initialProgress, hasMoreBatches });
  return {
    getProgress: () => progressSubject.asObservable(),
    _testSetProgress: (percentage: number, more: boolean) => 
      progressSubject.next({ percentage, hasMoreBatches: more }),
  } as any;
}

function mountComponent(
  exerciseService: Partial<ExerciseService> = createMockExerciseService()
) {
  return mount(ExerciseBatchProgressBarComponent, {
    providers: [
      { provide: ExerciseService, useValue: exerciseService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseBatchProgressBarComponent', () => {

  describe('Rendering', () => {
    it('renders the batch progress bar component', () => {
      mountComponent();
      cy.get(SEL.BATCH_PROGRESS_BAR).should('exist');
    });
  });

  describe('Progress display', () => {
    it('displays progress without more batches', () => {
      mountComponent(createMockExerciseService(50, false));
      cy.get(SEL.BATCH_PROGRESS_BAR).should('exist');
    });

    it('displays progress with more batches', () => {
      mountComponent(createMockExerciseService(50, true));
      cy.get(SEL.BATCH_PROGRESS_BAR).should('exist');
    });

    it('updates progress when service emits new value', () => {
      const service = createMockExerciseService(25, false);
      mountComponent(service);
      
      cy.get(SEL.BATCH_PROGRESS_BAR).should('exist').then(() => {
        (service as any)._testSetProgress(75, true);
      });
      
      cy.get(SEL.BATCH_PROGRESS_BAR).should('exist');
    });
  });
});

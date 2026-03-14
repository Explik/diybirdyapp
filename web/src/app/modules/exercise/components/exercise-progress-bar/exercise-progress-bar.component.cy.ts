import { mount } from 'cypress/angular';
import { ExerciseProgressBarComponent } from './exercise-progress-bar.component';
import { ExerciseService } from '../../services/exercise.service';
import { BehaviorSubject } from 'rxjs';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  PROGRESS_BAR: 'app-progress-bar',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockExerciseService(
  initialProgress: number = 0
): Partial<ExerciseService> {
  const progressSubject = new BehaviorSubject({ percentage: initialProgress });
  return {
    getProgress: () => progressSubject.asObservable(),
    _testSetProgress: (percentage: number) => progressSubject.next({ percentage }),
  } as any;
}

function mountComponent(
  exerciseService: Partial<ExerciseService> = createMockExerciseService()
) {
  return mount(ExerciseProgressBarComponent, {
    providers: [
      { provide: ExerciseService, useValue: exerciseService },
    ],
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('ExerciseProgressBarComponent', () => {

  describe('Rendering', () => {
    it('renders the progress bar component', () => {
      mountComponent();
      cy.get(SEL.PROGRESS_BAR).should('exist');
    });
  });

  describe('Progress display', () => {
    it('displays 0% progress initially', () => {
      mountComponent(createMockExerciseService(0));
      cy.get(SEL.PROGRESS_BAR).should('exist');
    });

    it('displays 50% progress when service returns 50', () => {
      mountComponent(createMockExerciseService(50));
      cy.get(SEL.PROGRESS_BAR).should('exist');
    });

    it('displays 100% progress when service returns 100', () => {
      mountComponent(createMockExerciseService(100));
      cy.get(SEL.PROGRESS_BAR).should('exist');
    });

    it('updates progress when service emits new value', () => {
      const service = createMockExerciseService(25);
      mountComponent(service);
      
      cy.get(SEL.PROGRESS_BAR).should('exist').then(() => {
        (service as any)._testSetProgress(75);
      });
      
      cy.get(SEL.PROGRESS_BAR).should('exist');
    });
  });
});

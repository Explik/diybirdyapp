import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { take } from 'rxjs';
import { InstructionComponent } from '../../components/instruction/instruction.component';
import { ExerciseInputMultiStagePairOptionsComponent } from '../../components/exercise-input-multi-stage-pair-options/exercise-input-multi-stage-pair-options.component';
import { ExerciseInputMultiStagePairOptionsDto } from '../../../../shared/api-client/model/exercise-input-multi-stage-pair-options-dto';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseSessionDataService } from '../../services/exerciseSessionData.service';

@Component({
  selector: 'app-exercise-content-multi-stage-tap-pairs-container',
  imports: [CommonModule, InstructionComponent, ExerciseInputMultiStagePairOptionsComponent],
  templateUrl: './exercise-content-multi-stage-tap-pairs-container.component.html'
})
export class ExerciseContentMultiStageTapPairsContainerComponent implements OnInit {
  input?: ExerciseInputMultiStagePairOptionsDto;

  private exerciseId?: string;
  private sessionId?: string;

  constructor(
    private exerciseService: ExerciseService,
    private dataService: ExerciseSessionDataService
  ) {}

  ngOnInit(): void {
    this.exerciseService.getInput<ExerciseInputMultiStagePairOptionsDto>().subscribe(data => {
      if (data) this.input = data;
    });

    this.exerciseService.getExercise().subscribe(exercise => {
      this.exerciseId = exercise?.id;
    });

    this.exerciseService.getExerciseSession().subscribe(session => {
      this.sessionId = session?.id;
    });
  }

  async onPairSelected(selected: { leftId: string; rightId: string }): Promise<void> {
    if (!this.input || !this.exerciseId || !this.sessionId) return;

    // Build the answer payload: type discriminator, session context, and the selected pair
    const answer = {
      type: 'multi-stage-pair-options',
      sessionId: this.sessionId,
      selectedPair: selected
    };

    this.dataService.submitExerciseAnswer(this.exerciseId, answer).pipe(take(1)).subscribe({
      next: async exerciseDto => {
        const updatedInput = exerciseDto.input as ExerciseInputMultiStagePairOptionsDto;
        if (!updatedInput) return;

        // Pass updated input (with feedback) to the input component
        this.input = updatedInput;

        // If exercise is complete, advance after the feedback flash has settled.
        // Two conditions trigger completion:
        //  1. answeredCount has reached the configured maxPairs limit, OR
        //  2. A correct answer was given but the server has no replacement pair left
        //     (replacementLeft / replacementRight are both absent in the feedback).
        const fb = updatedInput.feedback;
        const noMorePairs = !!fb?.correctPairs?.length && !fb.replacementLeft && !fb.replacementRight;
        if (updatedInput.answeredCount >= updatedInput.maxPairs || noMorePairs) {
          setTimeout(async () => {
            await this.exerciseService.nextExerciseAsync();
          }, 800);
        }
      },
      error: err => {
        console.error('Error submitting multi-stage pair answer', err);
        // Re-enable the input component by resetting the input reference
        this.input = { ...this.input! };
      }
    });
  }
}

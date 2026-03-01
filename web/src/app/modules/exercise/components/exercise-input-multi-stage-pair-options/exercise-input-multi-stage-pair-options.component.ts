import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import {
  ExerciseInputMultiStagePairOptionsDto,
  MultiStagePairOptionsOption
} from '../../../../shared/api-client/model/exercise-input-multi-stage-pair-options-dto';

type VisualState = 'default' | 'selected' | 'correct' | 'incorrect';

interface OptionDecoration {
  option: MultiStagePairOptionsOption | null;  // null = empty slot (blank placeholder)
  state: VisualState;
}

@Component({
  selector: 'app-exercise-input-multi-stage-pair-options',
  imports: [CommonModule],
  templateUrl: './exercise-input-multi-stage-pair-options.component.html',
  styleUrl: './exercise-input-multi-stage-pair-options.component.css'
})
export class ExerciseInputMultiStagePairOptionsComponent implements OnChanges {
  @Input({ required: true }) input!: ExerciseInputMultiStagePairOptionsDto;

  /** Emitted each time the user selects a complete pair. */
  @Output() pairSelected = new EventEmitter<{ leftId: string; rightId: string }>();

  selectedLeftId: string | null = null;
  selectedRightId: string | null = null;

  /**
   * Local copy of the option lists used for rendering.
   * Null entries represent empty/blank slots that preserve grid position.
   * These are only updated AFTER feedback flash completes so the matched buttons
   * remain visible during the flash animation.
   */
  displayedLeft: (MultiStagePairOptionsOption | null)[] = [];
  displayedRight: (MultiStagePairOptionsOption | null)[] = [];

  /**
   * Holds the latest server response while its options are waiting to be applied
   * (i.e. until after the feedback flash finishes).
   */
  private pendingInput: ExerciseInputMultiStagePairOptionsDto | null = null;

  /**
   * While an answer is in-flight (or the exercise is complete) interactions
   * are blocked. The container sets this back to false by passing a new `input`.
   */
  isSubmitting = false;

  /** Transient highlight set while showing brief feedback (~600 ms). */
  private feedbackLeftId: string | null = null;
  private feedbackRightId: string | null = null;
  private feedbackType: 'correct' | 'incorrect' | null = null;

  // -----------------------------------------------------------------------
  // Lifecycle
  // -----------------------------------------------------------------------

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['input']) return;

    const newInput: ExerciseInputMultiStagePairOptionsDto = changes['input'].currentValue;
    if (!newInput) return;

    // Re-enable interactions once a new input arrives
    this.isSubmitting = false;

    const fb = newInput.feedback;
    if (fb) {
      // Store the updated options for later; keep showing the current displayed options
      // so the matched pair is still visible throughout the flash animation
      this.pendingInput = newInput;

      if (fb.correctPairs?.length) {
        const pair = fb.correctPairs[0];
        this.showFeedback(pair.leftId, pair.rightId, 'correct', () => {
          this.applyPendingInput();
        });
      } else if (fb.incorrectPairs?.length) {
        const pair = fb.incorrectPairs[0];
        this.showFeedback(pair.leftId, pair.rightId, 'incorrect', () => {
          this.applyPendingInput();
        });
      }
    } else {
      // No feedback: initial load or exercise complete — sync displayed options immediately
      this.syncDisplayed(newInput);
    }
  }

  // -----------------------------------------------------------------------
  // Template helpers
  // -----------------------------------------------------------------------

  get leftDecorations(): OptionDecoration[] {
    return this.displayedLeft.map(o => ({ option: o, state: o ? this.stateFor(o.id, 'left') : 'default' }));
  }

  get rightDecorations(): OptionDecoration[] {
    return this.displayedRight.map(o => ({ option: o, state: o ? this.stateFor(o.id, 'right') : 'default' }));
  }

  /** Zips left and right decorations into rows for the CSS grid. */
  get rows(): { left: OptionDecoration; right: OptionDecoration }[] {
    const len = Math.max(this.displayedLeft.length, this.displayedRight.length);
    const blank: OptionDecoration = { option: null, state: 'default' };
    return Array.from({ length: len }, (_, i) => ({
      left:  this.leftDecorations[i]  ?? blank,
      right: this.rightDecorations[i] ?? blank
    }));
  }

  get isComplete(): boolean {
    return this.input.answeredCount >= this.input.maxPairs;
  }

  isDisabled(_id: string): boolean {
    return this.isSubmitting || this.isComplete || this.feedbackType !== null;
  }

  getButtonClass(state: VisualState): string {
    return state; // CSS classes match state names
  }

  // -----------------------------------------------------------------------
  // User interactions
  // -----------------------------------------------------------------------

  selectLeft(option: MultiStagePairOptionsOption): void {
    if (!option || this.isDisabled(option.id)) return;
    this.selectedLeftId = option.id;
    this.tryEmitPair();
  }

  selectRight(option: MultiStagePairOptionsOption): void {
    if (!option || this.isDisabled(option.id)) return;
    this.selectedRightId = option.id;
    this.tryEmitPair();
  }

  // -----------------------------------------------------------------------
  // Private helpers
  // -----------------------------------------------------------------------

  private tryEmitPair(): void {
    if (this.selectedLeftId && this.selectedRightId) {
      this.isSubmitting = true;
      this.pairSelected.emit({ leftId: this.selectedLeftId, rightId: this.selectedRightId });
    }
  }

  private stateFor(id: string, _side: 'left' | 'right'): VisualState {
    if (this.feedbackType && (this.feedbackLeftId === id || this.feedbackRightId === id)) {
      return this.feedbackType; // 'correct' or 'incorrect'
    }
    if (id === this.selectedLeftId || id === this.selectedRightId) {
      return 'selected';
    }
    return 'default';
  }

  private showFeedback(
    leftId: string,
    rightId: string,
    type: 'correct' | 'incorrect',
    onDone: () => void
  ): void {
    this.feedbackLeftId = leftId;
    this.feedbackRightId = rightId;
    this.feedbackType = type;

    setTimeout(() => {
      this.feedbackLeftId = null;
      this.feedbackRightId = null;
      this.feedbackType = null;
      onDone();
    }, 600);
  }

  /** Apply the pending server response and clear selection. */
  private applyPendingInput(): void {
    const pending = this.pendingInput;
    this.pendingInput = null;

    const fb = pending?.feedback;
    if (fb?.correctPairs?.length) {
      const pair = fb.correctPairs[0];

      // Replace matched slot in-place: use replacement if provided, otherwise leave null (blank)
      const leftIdx  = this.displayedLeft.findIndex(o => o?.id === pair.leftId);
      const rightIdx = this.displayedRight.findIndex(o => o?.id === pair.rightId);
      if (leftIdx  !== -1) this.displayedLeft[leftIdx]  = fb.replacementLeft  ?? null;
      if (rightIdx !== -1) this.displayedRight[rightIdx] = fb.replacementRight ?? null;
    }

    this.clearSelection();
  }

  /** Sync the local displayed option lists from a server input. */
  private syncDisplayed(input: ExerciseInputMultiStagePairOptionsDto): void {
    this.displayedLeft  = [...(input.leftOptions  ?? [])];
    this.displayedRight = [...(input.rightOptions ?? [])];
  }

  private clearSelection(): void {
    this.selectedLeftId = null;
    this.selectedRightId = null;
  }
}

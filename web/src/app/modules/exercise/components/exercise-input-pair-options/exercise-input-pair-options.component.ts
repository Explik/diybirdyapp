import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ExerciseInputPairOptionsDto, PairOptionFeedbackPair } from '../../../../shared/api-client';
import { SelectOptionInputTextOption } from '../../../../shared/api-client/model/select-option-input-text-option';

@Component({
  selector: 'app-exercise-input-pair-options',
  imports: [CommonModule],
  templateUrl: './exercise-input-pair-options.component.html',
  styleUrl: './exercise-input-pair-options.component.css'
})
export class ExerciseInputPairOptionsComponent {
  selectedLeft: any = null;
  selectedRight: any = null;
  selectedPair?: { left: string, right: string } = undefined;

  @Input({required: true}) input!: ExerciseInputPairOptionsDto; 

  get leftOptions() {
    return this.input.leftOptions as any as SelectOptionInputTextOption[];
  }

  get rightOptions() {
    return this.input.rightOptions as any as SelectOptionInputTextOption[];
  }

  get correctPairs(): PairOptionFeedbackPair[] {
    return this.input.feedback?.correctPairs ?? [];
  }

  get incorrectPairs() {
    return this.input.feedback?.incorrectPairs ?? [];
  }

  selectOption(option: any, side: 'left' | 'right') {
    if (side === 'left') {
      this.selectedLeft = option;

      if (this.selectedPair && this.selectedPair.left !== option.id) {
        this.selectedPair = undefined;
        this.selectedRight = undefined;
      }
    } else {
      this.selectedRight = option;

      if (this.selectedPair && this.selectedPair.right !== option.id) {
        this.selectedPair = undefined;
        this.selectedLeft = undefined;
      }
    }

    if (this.selectedLeft && this.selectedRight) {
      this.selectedPair = { left: this.selectedLeft.id, right: this.selectedRight.id };
    }
  }

  getOptionClass(option: any, side: 'left' | 'right') {
    const optionId = option.id;

    const isCorrect = this.correctPairs.some(p => p.leftId === optionId || p.rightId === optionId);
    if (isCorrect) 
      return 'correct';
    
    const isIncorrect = this.incorrectPairs.some(p => p.leftId === optionId || p.rightId === optionId);
    if (isIncorrect)
      return 'incorrect';

    const isSelected = side === 'left' ? this.selectedLeft === option : this.selectedRight === option;
    if (isSelected)
      return 'selected';

    return '';
  }

  isOptionDisabled(option: any) {
    const optionId = option.id;
    const isCorrect = this.correctPairs.some(p => p.leftId === optionId || p.rightId === optionId);
    const isIncorrect = this.incorrectPairs.some(p => p.leftId === optionId || p.rightId === optionId);

    return isCorrect || isIncorrect;
  }
}

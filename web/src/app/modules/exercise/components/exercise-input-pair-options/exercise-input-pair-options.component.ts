import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-exercise-input-pair-options',
  imports: [CommonModule],
  templateUrl: './exercise-input-pair-options.component.html',
  styleUrl: './exercise-input-pair-options.component.css'
})
export class ExerciseInputPairOptionsComponent {
  selectedPair?: { left: string, right: string } = undefined;
  
  @Input() leftOptions: { id: string, text: string }[] = [];
  @Input() rightOptions: { id: string, text: string }[] = [];
  
  @Input() correctPairs: { left: string, right: string }[] = [];
  @Input() incorrectPairs: { left: string, right: string }[] = [];

  selectedLeft: any = null;
  selectedRight: any = null;

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

    const isCorrect = this.correctPairs.some(p => p.left === optionId || p.right === optionId);
    if (isCorrect) 
      return 'correct';
    
    const isIncorrect = this.incorrectPairs.some(p => p.left === optionId || p.right === optionId);
    if (isIncorrect)
      return 'incorrect';

    const isSelected = side === 'left' ? this.selectedLeft === option : this.selectedRight === option;
    if (isSelected)
      return 'selected';

    return '';
  }

  isOptionDisabled(option: any) {
    const optionId = option.id;
    const isCorrect = this.correctPairs.some(p => p.left === optionId || p.right === optionId);
    const isIncorrect = this.incorrectPairs.some(p => p.left === optionId || p.right === optionId);

    return isCorrect || isIncorrect;
  }
}

import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-exercise-input-arrange-text-options',
  imports: [CommonModule],
  templateUrl: './exercise-input-arrange-text-options.component.html'
})
export class ExerciseInputArrangeTextOptionsComponent {
  @Input() input: ExerciseInputArrangeTextOptionsDto | undefined = undefined;

  selectedOptionIds: string[] = [];

  get options(): ArrangeTextOption[] {
    return this.input?.options || [];
  }

  get emptySlots(): undefined[] {
    return Array(this.options.length - this.selectedOptionIds.length);
  }

  addWord(id: string): void {
    if (!this.selectedOptionIds.includes(id)) {
      this.selectedOptionIds.push(id);
    }
  }

  removeWord(index: number): void {
    this.selectedOptionIds.splice(index, 1);
  }

  getOptionTextById(id: string): string {
    const option = this.options.find(option => option.id === id);
    return option ? option.text : '';
  }
}

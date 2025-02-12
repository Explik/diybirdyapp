import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExerciseInputMultipleChoiceTextDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-input-select-options',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './exercise-input-select-options.component.html',
  styleUrl: './exercise-input-select-options.component.css'
})
export class ExerciseInputSelectOptionsComponent {
  @Input() input: ExerciseInputMultipleChoiceTextDto | undefined = undefined;
  @Output()  optionSelected: EventEmitter<string> = new EventEmitter<string>();

  handleOptionSelected(optionId: string): void {
    if (this.input && !this.input.feedback)
      this.optionSelected.emit(optionId);
  }
}

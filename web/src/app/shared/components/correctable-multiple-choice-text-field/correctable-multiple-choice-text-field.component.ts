import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-correctable-multiple-choice-text-field',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './correctable-multiple-choice-text-field.component.html',
  styleUrl: './correctable-multiple-choice-text-field.component.css'
})
export class CorrectableMultipleChoiceTextFieldComponent {
  @Input() input: ExerciseInputMultipleChoiceTextDto | undefined = undefined;
  @Output()  optionSelected: EventEmitter<string> = new EventEmitter<string>();

  handleOptionSelected(optionId: string): void {
    if (this.input && !this.input.feedback)
      this.optionSelected.emit(optionId);
  }
}

import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
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
}

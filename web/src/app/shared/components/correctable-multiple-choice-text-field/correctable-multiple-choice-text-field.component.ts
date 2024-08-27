import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MultipleChoiceTextInput } from '../../models/input.interface';

@Component({
  selector: 'app-correctable-multiple-choice-text-field',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './correctable-multiple-choice-text-field.component.html',
  styleUrl: './correctable-multiple-choice-text-field.component.css'
})
export class CorrectableMultipleChoiceTextFieldComponent {
  @Input() state: "input" | "result" = "result";
  @Input() input: MultipleChoiceTextInput | undefined = undefined; 
}

import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MultipleChoiceOption } from '../../models/exercise.interface';

@Component({
  selector: 'app-select-option-field',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './select-option-field.component.html',
  styleUrl: './select-option-field.component.css'
})
export class SelectOptionFieldComponent {
  @Input() state: "input" | "result" = "result";
  @Input() options: MultipleChoiceOption[] = []
}

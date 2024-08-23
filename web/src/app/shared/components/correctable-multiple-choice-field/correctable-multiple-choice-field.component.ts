import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-correctable-multiple-choice-field',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './correctable-multiple-choice-field.component.html',
  styleUrl: './correctable-multiple-choice-field.component.css'
})
export class CorrectableMultipleChoiceFieldComponent {
  @Input() state: "input" | "result" = "result";
  @Input() options: Record<string, string> = {}
}

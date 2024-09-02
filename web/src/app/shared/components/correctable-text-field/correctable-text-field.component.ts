import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TextInput, TextInputFeedback } from '../../models/input.interface';

@Component({
  selector: 'app-correctable-text-field',
  standalone: true,
  templateUrl: './correctable-text-field.component.html',
  styleUrls: ['./correctable-text-field.component.css'],
  imports: [CommonModule, FormsModule]
})
export class CorrectableTextFieldComponent {
  @Input({required: true}) value!: TextInput;
  @Input() feedback?: TextInputFeedback;
}
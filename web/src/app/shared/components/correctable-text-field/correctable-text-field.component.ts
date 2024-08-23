import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { CorrectableTextInput } from '../../models/input.interface';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-correctable-text-field',
  standalone: true,
  templateUrl: './correctable-text-field.component.html',
  styleUrls: ['./correctable-text-field.component.css'],
  imports: [CommonModule, FormsModule]
})
export class CorrectableTextFieldComponent {
  @Input({required: true}) value!: CorrectableTextInput;
}
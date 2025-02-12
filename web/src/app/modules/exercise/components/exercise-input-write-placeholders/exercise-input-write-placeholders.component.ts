import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExerciseInputWritePlaceholdersDto, WritePlaceholdersFeedbackDto, WritePlaceholdersPartDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-input-write-placeholders',
  imports: [CommonModule, FormsModule],
  templateUrl: './exercise-input-write-placeholders.component.html'
})
export class ExerciseInputWritePlaceholdersComponent {
  @Input({required: true}) input!: ExerciseInputWritePlaceholdersDto; 

  get parts(): WritePlaceholdersPartDto[] {
    return this.input.parts!;
  }

  get feedback(): WritePlaceholdersFeedbackDto | undefined {
    return this.input.feedback;
  }
}

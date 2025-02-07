import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-exercise-input-write-placeholders',
  imports: [CommonModule],
  templateUrl: './exercise-input-write-placeholders.component.html'
})
export class ExerciseInputWritePlaceholdersComponent {
  @Input() parts: { type: string, value?: string, size?: number }[] = [];
}

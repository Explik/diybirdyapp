import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-dynamic-text-content',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dynamic-text-content.component.html'
})
export class DynamicTextContentComponent {
  @Input() data?: ExerciseContentTextDto;
}

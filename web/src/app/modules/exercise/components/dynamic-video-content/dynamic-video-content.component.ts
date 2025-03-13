import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ExerciseContentVideoDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-dynamic-video-content',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dynamic-video-content.component.html'
})
export class DynamicVideoContentComponent {
  @Input() data?: ExerciseContentVideoDto;
}

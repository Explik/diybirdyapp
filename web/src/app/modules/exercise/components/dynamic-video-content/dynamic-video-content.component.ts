
import { Component, Input } from '@angular/core';
import { ExerciseContentVideoDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-dynamic-video-content',
  standalone: true,
  imports: [],
  templateUrl: './dynamic-video-content.component.html'
})
export class DynamicVideoContentComponent {
  @Input() data?: ExerciseContentVideoDto;
}

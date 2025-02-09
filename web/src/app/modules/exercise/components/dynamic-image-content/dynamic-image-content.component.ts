import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-dynamic-image-content',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dynamic-image-content.component.html',
  styleUrl: './dynamic-image-content.component.css'
})
export class DynamicImageContentComponent {
  @Input() data?: ExerciseContentImageDto;
}

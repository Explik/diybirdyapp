import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './container.component.html'
})
export class ContainerComponent {
  @Input() layout: 'single' | 'double' = 'single';
}

import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-label',
  imports: [],
  templateUrl: './label.component.html'
})
export class LabelComponent {
  @Input() for?: string;
}

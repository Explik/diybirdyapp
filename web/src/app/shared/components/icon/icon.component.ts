import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-icon',
  standalone: true,
  imports: [],
  template: `<span [class]="'mdi ' + icon + ' ' + size + ' ' + color"></span>`
})
export class IconComponent {
  @Input() icon: string = ''; // The mdi icon name, e.g., 'mdi-account'
  @Input() size: string = 'text-base'; // Tailwind size classes
  @Input() color: string = 'text-black'; // Tailwind color classes
}

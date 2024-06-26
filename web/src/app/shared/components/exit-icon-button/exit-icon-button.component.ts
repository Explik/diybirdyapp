import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-exit-icon-button',
  standalone: true,
  imports: [],
  templateUrl: './exit-icon-button.component.html',
  styleUrl: './exit-icon-button.component.css'
})
export class ExitIconButtonComponent {
  @Output() click = new EventEmitter<void>();

  handleClick() {
    this.click.emit();
  }
}

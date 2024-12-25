import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-text-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './text-button.component.html',
  styleUrl: './text-button.component.css'
})
export class TextButtonComponent {
  @Input() label: string = 'Check Answer';
  @Input() state: 'primary' | 'secondary' = 'primary';
  @Input() disabled: boolean = false;

  @Output() click = new EventEmitter<void>();

  handleClick() {
    this.click.emit();
  }
}
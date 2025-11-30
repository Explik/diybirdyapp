import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-row-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './row-button.component.html',
  styleUrl: './row-button.component.css'
})
export class RowButtonComponent {
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() disabled = false;
  @Input() ariaLabel: string | null = null;
  @Input() title: string | null = null;
  @Input() name: string | null = null;
  @Input() value: string | null = null;
  @Input() tabindex: number | null = null;
  @Input() autofocus = false;
  @Input() form: string | null = null;
  @Input() formaction: string | null = null;
  @Input() formenctype: string | null = null;
  @Input() formmethod: string | null = null;
  @Input() formnovalidate: boolean | null = null;
  @Input() formtarget: string | null = null;
  @Input() layout: 'single' | 'double' = 'single';
  
  @Output() buttonClick = new EventEmitter<MouseEvent>();
  
  onClick(event: MouseEvent): void {
    if (!this.disabled) {
      this.buttonClick.emit(event);
    }
  }
}
import { Component, ElementRef, HostListener, Input, OnInit, Optional } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectComponent } from '../select/select.component';

@Component({
  selector: 'app-option',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './option.component.html',
  host: {
    class: 'block w-full'
  }
})
export class OptionComponent implements OnInit {
  @Input({ required: true }) value!: any;
  @Input() label?: string;
  @Input() disabled: boolean = false;

  constructor(
    private elementRef: ElementRef,
    @Optional() private select: SelectComponent
  ) {}

  ngOnInit(): void {
    // If no label provided, use the content of the element as label
    if (!this.label && this.elementRef.nativeElement.textContent) {
      this.label = this.elementRef.nativeElement.textContent.trim();
    }
  }

  @HostListener('click', ['$event'])
  onClick(event: Event): void {
    event.stopPropagation();
    if (!this.disabled && this.select) {
      this.select.selectOption(this);
    }
  }

  get isSelected(): boolean {
    return this.select && this.select.selectedValue === this.value;
  }
}

import { Component, ContentChildren, forwardRef, HostBinding, HostListener, Input, OnInit, QueryList, AfterContentInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { OptionComponent } from '../option/option.component';
import { AppFormFieldControl } from '../../models/form.interface';

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './select.component.html',
  host: {
    class: 'block w-full relative'
  },
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectComponent),
      multi: true
    }
  ]
})
export class SelectComponent implements ControlValueAccessor, OnInit, AfterContentInit, AppFormFieldControl {
  @Input() id: string | undefined;
  @Input() placeholder: string = 'Select an option';
  @Input() disabled: boolean = false;
  @Input() required: boolean = false;
  @ContentChildren(OptionComponent) options!: QueryList<OptionComponent>;
  
  isOpen: boolean = false;
  selectedValue: any = null;
  selectedLabel: string = '';
  
  // ControlValueAccessor implementation
  private onChange: (value: any) => void = () => {};
  private onTouched: () => void = () => {};
  
  ngOnInit(): void {}
  
  ngAfterContentInit(): void {
    // Update selected label after content is initialized
    this.updateSelectedLabel();
  }
  
  @HostBinding('class.pointer-events-none')
  @HostBinding('class.opacity-50')
  get isDisabled(): boolean {
    return this.disabled;
  }
  
  @HostListener('click')
  toggle(): void {
    if (!this.disabled) {
      this.isOpen = !this.isOpen;
      if (this.isOpen) {
        this.onTouched();
      }
    }
  }
  
  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event): void {
    const selectElement = (event.target as HTMLElement).closest('app-select');
    if (!selectElement) {
      this.isOpen = false;
    }
  }
  
  selectOption(option: OptionComponent): void {
    if (this.disabled) return;
    
    this.selectedValue = option.value;
    this.selectedLabel = option.label || option.value?.toString() || '';
    this.onChange(this.selectedValue);
    this.onTouched();
    this.isOpen = false;
  }
  
  private updateSelectedLabel(): void {
    if (this.selectedValue !== null && this.options) {
      const selectedOption = this.options.find(option => option.value === this.selectedValue);
      if (selectedOption) {
        this.selectedLabel = selectedOption.label || selectedOption.value?.toString() || '';
      }
    }
  }
  
  // ControlValueAccessor methods
  writeValue(value: any): void {
    this.selectedValue = value;
    
    // Find matching option to get the label
    setTimeout(() => {
      this.updateSelectedLabel();
    });
  }
  
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }
  
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  
  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
  
  get displayValue(): string {
    return this.selectedLabel || this.placeholder;
  }
}

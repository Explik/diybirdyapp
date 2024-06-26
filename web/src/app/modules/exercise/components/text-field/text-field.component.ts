import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-text-field',
  standalone: true,
  templateUrl: './text-field.component.html',
  styleUrls: ['./text-field.component.css'],
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi:true,
      useExisting: TextFieldComponent
    }
  ]
})
export class TextFieldComponent implements OnInit, ControlValueAccessor {
  @Input() state: 'input' | 'result' = 'input';
  @Input() result: 'success' | 'failure' | null = null;
  
  value: string = '';
  onChange = (v: string) => {};
  onTouched = () => {};
  touched = false;
  disabled = false;

  writeValue(obj: any): void {
    this.value = obj as string;
  }
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
  
  ngOnInit() {
    // Ensure the component initializes with the correct state
    if (this.state === 'result' && !this.result) {
      throw new Error('Result state requires a result value of either "success" or "failure"');
    }
  }

  onUpdatedValue() {
    if (!this.touched) {
      this.onTouched();
      this.touched = true; 
    }
    if (!this.disabled) {
      this.onChange(this.value);
    }
  }
}
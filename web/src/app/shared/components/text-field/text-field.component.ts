import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-text-field',
  standalone: true,
  templateUrl: './text-field.component.html',
  styleUrl: './text-field.component.css',
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi:true,
      useExisting: TextFieldComponent
    }
  ]
})
export class TextFieldComponent {
  @Input() rows: number = 1;

  value: string = '';
  touched = false;
  disabled = false;

  onChange = (v: string) => {};
  onTouched = () => {};
  
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

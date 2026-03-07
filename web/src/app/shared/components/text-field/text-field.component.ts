import { CommonModule } from '@angular/common';
import { Component, forwardRef, Input } from '@angular/core';
import { FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { APP_FORM_FIELD_CONTROL, AppFormFieldControl } from '../../models/form.interface';

@Component({
  selector: 'app-text-field',
  standalone: true,
  templateUrl: './text-field.component.html',
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: TextFieldComponent
    },
    {
      provide: APP_FORM_FIELD_CONTROL,
      useExisting: forwardRef(() => TextFieldComponent)
    }
  ]
})
export class TextFieldComponent implements AppFormFieldControl {
  @Input() id: string | undefined;
  @Input() rows: number = 1;
  @Input() type: 'text' | 'password' | 'email' | 'number' = 'text';

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

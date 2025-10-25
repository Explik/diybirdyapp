import { Component, EventEmitter, Input, Output, forwardRef, OnChanges } from '@angular/core';
import { EditFlashcardTextImpl } from '../../models/editFlashcard.model';
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { CommonModule } from '@angular/common';
import { FormsModule, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-text-input',
  imports: [CommonModule, FormsModule, TextFieldComponent],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TextInputComponent),
      multi: true,
    },
  ],
  templateUrl: './text-input.component.html',
  styleUrl: './text-input.component.css'
})
export class TextInputComponent implements ControlValueAccessor, OnChanges {
  @Input() textData: EditFlashcardTextImpl|undefined = undefined; 
  @Output() textDataChange = new EventEmitter<EditFlashcardTextImpl|undefined>();
  textValue: string = "";

  // ControlValueAccessor callbacks
  private onChange: (val: any) => void = () => {};
  private onTouched: () => void = () => {};
  public disabled: boolean = false;

  ngOnChanges(): void {
    if (this.textData) {
      this.textValue = this.textData.text;
    }
  }

  /**
   * Called by the internal input when the value changes.
   * Updates textData, emits the output event, and notifies the registered form control.
   */
  updateTextValue(newValue: string): void {
    if (this.disabled) {
      return;
    }

    this.textValue = newValue;

    if (this.textData) {
      this.textData.text = newValue;
    } else {
      this.textData = new EditFlashcardTextImpl();
      this.textData.text = newValue;
    }

    // emit the legacy output for existing consumers
    this.textDataChange.emit(this.textData);

    // notify Angular forms
    this.onChange(this.textData);
  }

  // ControlValueAccessor interface
  writeValue(obj: EditFlashcardTextImpl | string | null): void {
    if (obj == null) {
      this.textData = undefined;
      this.textValue = '';
      return;
    }

    if (typeof obj === 'string') {
      this.textValue = obj;
      if (!this.textData) this.textData = new EditFlashcardTextImpl();
      this.textData.text = this.textValue;
    } else {
      this.textData = obj;
      this.textValue = obj.text ?? '';
    }
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
}

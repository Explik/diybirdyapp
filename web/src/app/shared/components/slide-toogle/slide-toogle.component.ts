import { Component, forwardRef, HostBinding, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-slide-toogle',
  templateUrl: './slide-toogle.component.html',
  styleUrls: ['./slide-toogle.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SlideToogleComponent),
      multi: true,
    },
  ],
})
export class SlideToogleComponent implements ControlValueAccessor {
  // internal value
  private _checked = false;

  @HostBinding('class.disabled') disabled = false;

  // optional input to set initial value from parent
  @Input()
  set checked(v: boolean) {
    this._checked = !!v;
  }
  get checked(): boolean {
    return this._checked;
  }

  // ControlValueAccessor callbacks
  onChange = (v: any) => {};
  onTouched = () => {};

  writeValue(obj: any): void {
    this._checked = !!obj;
  }
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  setDisabledState?(isDisabled: boolean): void {
    this.disabled = !!isDisabled;
  }

  toggle(): void {
    if (this.disabled) return;
    this._checked = !this._checked;
    this.onChange(this._checked);
    this.onTouched();
  }

  // called from keyboard
  handleKey(event: KeyboardEvent) {
    if (this.disabled) return;
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      this.toggle();
    }
  }
}

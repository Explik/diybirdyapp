import { EventEmitter } from '@angular/core';

export interface SessionOptionsComponent<T> {
  /** Input: current options object (staged) */
  options?: T;

  /** Output: emits when staged options change */
  optionsChange: EventEmitter<T>;
}

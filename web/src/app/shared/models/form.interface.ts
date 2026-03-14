import { InjectionToken } from '@angular/core';

export interface AppFormFieldControl {
  id: string | undefined;
}

export const APP_FORM_FIELD_CONTROL = new InjectionToken<AppFormFieldControl>('AppFormFieldControl');
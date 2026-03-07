import { AfterContentInit, Component, ContentChild, Inject, Input, Optional } from '@angular/core';
import { LabelComponent } from '../label/label.component';
import { APP_FORM_FIELD_CONTROL, AppFormFieldControl } from '../../models/form.interface';

@Component({
  selector: 'app-form-field',
  templateUrl: './form-field.component.html',
  styleUrl: './form-field.component.css'
})
export class FormFieldComponent implements AfterContentInit {
  @ContentChild(LabelComponent) labelComponent: LabelComponent | undefined;
  @ContentChild(APP_FORM_FIELD_CONTROL) controlComponent: AppFormFieldControl | undefined;

  @Input() fieldId!: string;

  ngAfterContentInit(): void {
    if (this.labelComponent && this.fieldId)
      this.labelComponent.for = this.fieldId;

    if (this.controlComponent && this.fieldId)
      this.controlComponent.id = this.fieldId;
  }
}

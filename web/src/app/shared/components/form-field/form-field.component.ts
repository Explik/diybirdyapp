import { AfterContentInit, Component, ContentChild, Input } from '@angular/core';
import { LabelComponent } from '../label/label.component';
import { TextFieldComponent } from '../text-field/text-field.component';

@Component({
  selector: 'app-form-field',
  templateUrl: './form-field.component.html',
  styleUrl: './form-field.component.css'
})
export class FormFieldComponent implements AfterContentInit {
  @ContentChild(LabelComponent) labelComponent: LabelComponent | undefined;
  @ContentChild(TextFieldComponent) controlComponent: TextFieldComponent | undefined;

  @Input() fieldId!: string;

  ngAfterContentInit(): void {
    if (this.labelComponent && this.fieldId) 
      this.labelComponent.for = this.fieldId;
  
    if (this.controlComponent && this.fieldId)
      this.controlComponent.id = this.fieldId;
  }
}

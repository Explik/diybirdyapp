import { AfterContentInit, Component, ContentChild } from '@angular/core';
import { LabelComponent } from '../label/label.component';
import { TextFieldComponent } from '../text-field/text-field.component';

let nextId = 0;

@Component({
  selector: 'app-form-field',
  templateUrl: './form-field.component.html',
  styleUrl: './form-field.component.css'
})
export class FormFieldComponent implements AfterContentInit {
  @ContentChild(LabelComponent) labelComponent: LabelComponent | undefined;
  @ContentChild(TextFieldComponent) controlComponent: TextFieldComponent | undefined;

  id = `form-input-${++nextId}`;

  ngAfterContentInit(): void {
    if (this.labelComponent) 
      this.labelComponent.for = this.id;
  
    if (this.controlComponent)
      this.controlComponent.id = this.id;
  }
}

import { Component } from '@angular/core';
import { FlashcardComponent } from "../../components/flashcard/flashcard.component";
import { IconComponent } from "../../components/icon/icon.component";
import { ProgressBarComponent } from "../../components/progress-bar/progress-bar.component";
import { FormFieldComponent } from "../../components/form-field/form-field.component";
import { LabelComponent } from "../../components/label/label.component";
import { TextFieldComponent } from "../../components/text-field/text-field.component";
import { SelectComponent } from "../../components/select/select.component";
import { OptionComponent } from "../../components/option/option.component";
import { ButtonComponent } from "../../components/button/button.component";
import { RowButtonComponent } from "../../components/row-button/row-button.component";

@Component({
  selector: 'app-shared-components-page',
  standalone: true,
  imports: [FlashcardComponent, IconComponent, ProgressBarComponent, FormFieldComponent, LabelComponent, TextFieldComponent, SelectComponent, OptionComponent, ButtonComponent, RowButtonComponent],
  templateUrl: './shared-components-page.component.html'
})
export class SharedComponentsPageComponent { }

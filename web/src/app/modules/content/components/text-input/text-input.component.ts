import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EditFlashcardTextImpl } from '../../models/editFlashcard.model';
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-text-input',
  imports: [CommonModule, FormsModule, TextFieldComponent],
  templateUrl: './text-input.component.html',
  styleUrl: './text-input.component.css'
})
export class TextInputComponent {
  @Input() textData: EditFlashcardTextImpl|undefined = undefined; 
  @Output() textDataChange = new EventEmitter<EditFlashcardTextImpl|undefined>();

  textValue: string = "";
  
  ngOnChanges(): void {
    if (this.textData) {
      this.textValue = this.textData.text;
    }
  }

  updateTextValue(newValue: string): void {
    if (this.textData) {
      this.textData.text = newValue;
      this.textDataChange.emit(this.textData);
    }
    else {
      this.textData = new EditFlashcardTextImpl();
      this.textData.text = newValue;
      this.textDataChange.emit(this.textData);
    }
  }
}

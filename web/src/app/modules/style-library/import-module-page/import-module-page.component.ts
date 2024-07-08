import { Component } from '@angular/core';
import { TranslationFlashcardComponent } from '../../import/components/translation-flashcard/translation-flashcard.component';
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-import-module-page',
  standalone: true,
  imports: [CommonModule, FormsModule, TextFieldComponent, TranslationFlashcardComponent],
  templateUrl: './import-module-page.component.html',
  styleUrl: './import-module-page.component.css'
})
export class ImportModulePageComponent {
  textFieldValue: string = "preset"
}

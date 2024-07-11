import { Component } from '@angular/core';
import { TranslationFlashcardComponent } from '../../import/components/translation-flashcard/translation-flashcard.component';
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslationFlashcard } from '../../import/models/flashcard.model';
import { FlashcardContainerComponent } from "../../import/components/flashcard-container/flashcard-container.component";
import { FlashcardPageComponent } from "../../import/components/flashcard-page/flashcard-page.component";

@Component({
    selector: 'app-import-module-page',
    standalone: true,
    templateUrl: './import-module-page.component.html',
    styleUrl: './import-module-page.component.css',
    imports: [CommonModule, FormsModule, TextFieldComponent, TranslationFlashcardComponent, FlashcardContainerComponent, FlashcardPageComponent]
})
export class ImportModulePageComponent {
  textFieldValue: string = "preset"

  flashcards: TranslationFlashcard[] = [
    { leftLabel: "DE", leftValue: "Hallo welt", rightLabel: "EN", rightValue: "Hello world" },
    { leftLabel: "DE", leftValue: "", rightLabel: "EN", rightValue: "" },
  ];
}

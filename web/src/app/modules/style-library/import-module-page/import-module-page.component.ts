import { Component } from '@angular/core';
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlashcardContainerComponent } from "../../import/components/flashcard-container/flashcard-container.component";
import { FlashcardDeckPageComponent } from "../../import/components/flashcard-deck-page/flashcard-deck-page.component";
import { Flashcard, FlashcardLanguage } from '../../import/models/flashcard.model';
import { GenericFlashcardComponent } from "../../import/components/generic-flashcard/generic-flashcard.component";

@Component({
    selector: 'app-import-module-page',
    standalone: true,
    templateUrl: './import-module-page.component.html',
    styleUrl: './import-module-page.component.css',
    imports: [CommonModule, FormsModule, TextFieldComponent, FlashcardContainerComponent, FlashcardDeckPageComponent, GenericFlashcardComponent]
})
export class ImportModulePageComponent {
  textFieldValue: string = "preset"

  languages: FlashcardLanguage[] = [
    { id: "1", abbreviation: "DE", name: "German" },
    { id: "2", abbreviation: "EN", name: "English" }
  ]

  flashcards: Flashcard[] = [
    { id: "2", deckId: "1", leftLanguage: this.languages[0], leftValue: "Hallo welt", rightLanguage: this.languages[1], rightValue: "Hello world" },
    { id: "3", deckId: "1", leftLanguage: this.languages[0], leftValue: "", rightLanguage: this.languages[1], rightValue: "" },
  ];
}

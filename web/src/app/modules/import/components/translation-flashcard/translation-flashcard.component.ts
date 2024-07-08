import { Component } from '@angular/core';
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-translation-flashcard',
    standalone: true,
    templateUrl: './translation-flashcard.component.html',
    styleUrl: './translation-flashcard.component.css',
    imports: [CommonModule, FormsModule, TextFieldComponent]
})
export class TranslationFlashcardComponent {

}

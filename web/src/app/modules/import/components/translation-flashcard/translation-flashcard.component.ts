import { Component, Input } from '@angular/core';
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslationFlashcard } from '../../models/flashcard.model';

@Component({
    selector: 'app-translation-flashcard',
    standalone: true,
    templateUrl: './translation-flashcard.component.html',
    styleUrl: './translation-flashcard.component.css',
    imports: [CommonModule, FormsModule, TextFieldComponent]
})
export class TranslationFlashcardComponent {
    @Input() flashcard: TranslationFlashcard = { leftLabel: "", leftValue: "", rightLabel: "", rightValue: "" };

    handleSwitchValues() {
        const temp = this.flashcard.leftValue;
        this.flashcard.leftValue = this.flashcard.rightValue;
        this.flashcard.rightValue = temp;
    } 
}

import { Component, OnInit } from '@angular/core';
import { FlashcardContainerComponent } from "../flashcard-container/flashcard-container.component";
import { TranslationFlashcard } from '../../models/flashcard.model';
import { ImportService } from '../../services/import.service';
import { zip } from 'rxjs';

@Component({
  selector: 'app-flashcard-page',
  standalone: true,
  imports: [FlashcardContainerComponent],
  templateUrl: './flashcard-page.component.html',
  styleUrl: './flashcard-page.component.css'
})
export class FlashcardPageComponent implements OnInit {
  originalFlashcards: TranslationFlashcard[] = []; 
  flashcards: TranslationFlashcard[] = [];

  constructor(private service: ImportService) {}

  ngOnInit(): void {
    this.service.getFlashcards().subscribe(data => {
      this.flashcards = data;

      // TODO Use proper deep copy
      this.originalFlashcards = JSON.parse(JSON.stringify(data));
    });
  }

  // TODO Add support for language selection
  addFlashcard() {
    const flashcard = { 
      leftLanguage: { id: "langVertex1" },
      rightLanguage: { id: "langVertex2" }
    };
    
    this.service.createFlashcard(flashcard).subscribe(data => {
      this.flashcards = [...this.flashcards, data];
    });
  }

  saveFlashcards() {
    const buffer = [];
    const maxLength = Math.max(this.originalFlashcards.length, this.flashcards.length);

    for(let i = 0; i < maxLength; i++) {
      const originalFlashcard = this.originalFlashcards[i];
      const currentFlashcard = this.flashcards[i];

      // TODO Add proper deep equal
      if (JSON.stringify(originalFlashcard) == JSON.stringify(currentFlashcard))
        continue;

      buffer.push(this.service.updateFlashcard(currentFlashcard));
    }

    zip(...buffer).subscribe(data => {
      this.originalFlashcards = JSON.parse(JSON.stringify(this.flashcards));
    });
  }
}

import { Component, OnInit } from '@angular/core';
import { FlashcardContainerComponent } from "../flashcard-container/flashcard-container.component";
import { Flashcard, FlashcardLanguage } from '../../models/flashcard.model';
import { ImportService } from '../../services/import.service';
import { zip } from 'rxjs';
import { RecursivePartial } from '../../../../shared/models/util.model';

@Component({
  selector: 'app-flashcard-page',
  standalone: true,
  imports: [FlashcardContainerComponent],
  templateUrl: './flashcard-page.component.html',
  styleUrl: './flashcard-page.component.css'
})
export class FlashcardPageComponent implements OnInit {
  originalFlashcards: Flashcard[] = []; 
  flashcards: Flashcard[] = [];
  flashcardLanguages: FlashcardLanguage[] = [];

  constructor(private service: ImportService) {}

  ngOnInit(): void {
    this.service.getFlashcards().subscribe(data => {
      this.flashcards = data;

      // TODO Use proper deep copy
      this.originalFlashcards = JSON.parse(JSON.stringify(data));
    });

    this.service.getFlashcardLanguages().subscribe(data => {
      this.flashcardLanguages = data;
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
      const currentFlashcard = this.flashcards[i] as RecursivePartial<FlashcardDto>;

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

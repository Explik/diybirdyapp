import { Component, OnInit } from '@angular/core';
import { FlashcardContainerComponent } from "../flashcard-container/flashcard-container.component";
import { Flashcard, FlashcardLanguage } from '../../models/flashcard.model';
import { ImportService } from '../../services/import.service';
import { zip } from 'rxjs';
import { RecursivePartial } from '../../../../shared/models/util.model';
import { ActivatedRoute, RouterModule } from '@angular/router';

@Component({
  selector: 'app-flashcard-deck-page',
  standalone: true,
  imports: [RouterModule, FlashcardContainerComponent],
  templateUrl: './flashcard-deck-page.component.html',
  styleUrl: './flashcard-deck-page.component.css'
})
export class FlashcardDeckPageComponent implements OnInit {
  originalFlashcards: Flashcard[] = []; 
  flashcardDeckId?: string = undefined;
  flashcards: Flashcard[] = [];
  flashcardLanguages: FlashcardLanguage[] = [];

  constructor(
    private route: ActivatedRoute, 
    private service: ImportService) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.flashcardDeckId = id ?? undefined;
      
      this.service.getFlashcards(id).subscribe(data => {
        this.flashcards = data;
  
        // TODO Use proper deep copy
        this.originalFlashcards = JSON.parse(JSON.stringify(data));
      });
    });

    this.service.getFlashcardLanguages().subscribe(data => {
      this.flashcardLanguages = data;
    });
  }

  // TODO Add support for language selection
  addFlashcard() {
    const flashcard = { 
      deckId: this.flashcardDeckId,
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

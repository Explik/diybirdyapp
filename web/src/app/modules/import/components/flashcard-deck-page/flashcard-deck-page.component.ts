import { Component, OnInit } from '@angular/core';
import { FlashcardContainerComponent } from "../flashcard-container/flashcard-container.component";
import { Flashcard, FlashcardLanguage } from '../../models/flashcard.model';
import { ImportService } from '../../services/import.service';
import { zip } from 'rxjs';
import { RecursivePartial } from '../../../../shared/models/util.model';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-flashcard-deck-page',
  standalone: true,
  imports: [RouterModule, FlashcardContainerComponent],
  templateUrl: './flashcard-deck-page.component.html',
  styleUrl: './flashcard-deck-page.component.css'
})
export class FlashcardDeckPageComponent implements OnInit {
  originalName?: string = undefined;
  originalFlashcards: Flashcard[] = []; 
  flashcardDeckId?: string = undefined;
  name?: string = undefined;
  flashcards: Flashcard[] = [];
  flashcardLanguages: FlashcardLanguage[] = [];

  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private service: ImportService) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.flashcardDeckId = id ?? undefined;
      
      if (id) {
        this.service.getFlashcardDeck(id).subscribe(data => {
          this.name = data.name;
          this.originalName = data.name;
        });
      }

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

    if (this.flashcardDeckId && this.originalName != this.name) {
      buffer.push(this.service.updateFlashcardDeck({ id: this.flashcardDeckId, name: this.name }));
    }

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
      this.originalName = this.name;
      this.originalFlashcards = JSON.parse(JSON.stringify(this.flashcards));
    });
  }

  reviewFlashcards() {
    this.service.reviewFlashcardDeck(this.flashcardDeckId!).subscribe(data => {
      this.router.navigate(['/session/' + data.id]);
    });
  }

  learnFlashcards() {
    this.service.learnFlashcardDeck(this.flashcardDeckId!).subscribe(data => {
      this.router.navigate(['/session/' + data.id]);
    });
  }
}

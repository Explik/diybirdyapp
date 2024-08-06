import { Component, OnInit } from '@angular/core';
import { FlashcardContainerComponent } from "../flashcard-container/flashcard-container.component";
import { TranslationFlashcard } from '../../models/flashcard.model';
import { ImportService } from '../../services/import.service';

@Component({
  selector: 'app-flashcard-page',
  standalone: true,
  imports: [FlashcardContainerComponent],
  templateUrl: './flashcard-page.component.html',
  styleUrl: './flashcard-page.component.css'
})
export class FlashcardPageComponent implements OnInit {
  flashcards: TranslationFlashcard[] = [];

  constructor(private service: ImportService) {}

  ngOnInit(): void {
    this.service.getFlashcards().subscribe(data => {
      this.flashcards = data;
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
    
  }
}

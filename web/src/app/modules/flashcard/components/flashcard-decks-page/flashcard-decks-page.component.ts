import { Component, OnInit } from '@angular/core';
import { FlashcardDeck } from '../../models/flashcard.model';
import { FlashcardService } from '../../services/flashcard.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-flashcard-decks-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './flashcard-decks-page.component.html',
  styleUrl: './flashcard-decks-page.component.css'
})
export class FlashcardDecksPageComponent implements OnInit {
  flashcardDecks: FlashcardDeck[] = [];

  constructor(private service: FlashcardService) {}

  ngOnInit(): void {
    this.service.getFlashcardDecks().subscribe(data => {
      this.flashcardDecks = data;
    });
  }

  addFlashcardDeck() {
    const flashcardDeck = { name: 'New deck' } as FlashcardDeck;

    this.service.createFlashcardDeck(flashcardDeck).subscribe(data => {
      this.flashcardDecks = [...this.flashcardDecks, data];
    });
  }
}

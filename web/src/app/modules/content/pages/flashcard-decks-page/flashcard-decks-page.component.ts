import { Component, OnInit } from '@angular/core';
import { FlashcardService } from '../../services/flashcard.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EditFlashcardDeck, EditFlashcardDeckImpl } from '../../models/editFlashcard.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-flashcard-decks-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './flashcard-decks-page.component.html',
  styleUrl: './flashcard-decks-page.component.css'
})
export class FlashcardDecksPageComponent implements OnInit {
  flashcardDecks: EditFlashcardDeck[] = [];

  constructor(private service: FlashcardService, private router: Router) {}

  ngOnInit(): void {
    this.service.getFlashcardDecks().subscribe(data => {
      this.flashcardDecks = data;
    });
  }

  addFlashcardDeck() {
    const flashcardDeck = { name: 'New deck' } as EditFlashcardDeck;

    this.service.createFlashcardDeck(flashcardDeck).subscribe(data => {
      //this.flashcardDecks = [...this.flashcardDecks, data];
      this.router.navigate([`/flashcard-deck/${data.id}/edit`]);
    });
  }
}

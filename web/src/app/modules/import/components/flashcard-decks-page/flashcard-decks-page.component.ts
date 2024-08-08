import { Component, OnInit } from '@angular/core';
import { FlashcardDeck } from '../../models/flashcard.model';
import { ImportService } from '../../services/import.service';
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

  constructor(private service: ImportService) {}

  ngOnInit(): void {
    this.service.getFlashcardDecks().subscribe(data => {
      this.flashcardDecks = data;
    });
  }
}

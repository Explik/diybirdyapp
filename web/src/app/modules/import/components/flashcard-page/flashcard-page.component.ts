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

  addFlashcard() {
    const leftLabel = this.flashcards.length ? this.flashcards[0].leftLabel : "";
    const rightLabel = this.flashcards.length ? this.flashcards[0].rightLabel : "";

    this.flashcards = [...this.flashcards, { leftLabel, rightLabel, leftValue: "", rightValue: "" }];
  }

  saveFlashcards() {
    
  }
}

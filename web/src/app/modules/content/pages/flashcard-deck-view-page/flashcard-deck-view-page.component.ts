import { Component, OnInit } from '@angular/core';
import { FlashcardEditContainerComponent } from "../../components/flashcard-edit-container/flashcard-edit-container.component";
import { FlashcardService } from '../../services/flashcard.service';
import { zip } from 'rxjs';
import { RecursivePartial } from '../../../../shared/models/util.model';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FlashcardReviewComponent } from "../../components/flashcard-review/flashcard-review.component";
import { CommonModule } from '@angular/common';
import { FlashcardReviewContainerComponent } from '../../components/flashcard-review-container/flashcard-review-container.component';
import { EditFlashcard, EditFlashcardDeckImpl, EditFlashcardImpl, EditFlashcardLanguage, EditFlashcardLanguageImpl } from '../../models/editFlashcard.model';
import { TextButtonComponent } from '../../../../shared/components/text-button/text-button.component';
import { FlashcardViewContainerComponent } from '../../components/flashcard-view-container/flashcard-view-container.component';

@Component({
  selector: 'app-flashcard-deck-view-page',
  standalone: true,
  imports: [RouterModule, CommonModule, FlashcardViewContainerComponent, FlashcardReviewContainerComponent, TextButtonComponent],
  templateUrl: './flashcard-deck-view-page.component.html',
  styleUrl: './flashcard-deck-view-page.component.css'
})
export class FlashcardDeckViewPageComponent implements OnInit {
  flashcardDeck?: EditFlashcardDeckImpl;
  flashcardLanguages: EditFlashcardLanguageImpl[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: FlashcardService) { }
  
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (id) {
        zip(
          this.service.getFlashcardDeck(id),
          this.service.getFlashcards(id)
        ).subscribe(([deck, flashcards]) => {
          this.flashcardDeck = deck;
          this.flashcardDeck.flashcards = flashcards;
        });
      }
    });

    this.service.getFlashcardLanguages().subscribe(data => {
      this.flashcardLanguages = data;
    });
  }

  get hasFlashcards(): boolean {
    return !!this.flashcardDeck && this.flashcardDeck.flashcards.length > 0;
  }

  selectFlashcards() {
    this.service.selectFlashcardDeck(this.flashcardDeck!.id).subscribe(data => {
      this.router.navigate(['/session/' + data.id]);
    });
  }

  reviewFlashcards() {
    this.service.reviewFlashcardDeck(this.flashcardDeck!.id).subscribe(data => {
      this.router.navigate(['/session/' + data.id]);
    });
  }

  writeFlashcards() {
    this.service.writeFlashcardDeck(this.flashcardDeck!.id).subscribe(data => {
      this.router.navigate(['/session/' + data.id]);
    });
  }

  learnFlashcards() {
    this.service.learnFlashcardDeck(this.flashcardDeck!.id).subscribe(data => {
      this.router.navigate(['/session/' + data.id]);
    });
  }

  editFlashcards() {
    this.router.navigate(['/flashcard-deck/' + this.flashcardDeck!.id + '/edit']);
  }
}

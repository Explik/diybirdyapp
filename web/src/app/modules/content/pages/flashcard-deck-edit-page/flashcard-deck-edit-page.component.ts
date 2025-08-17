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

@Component({
  selector: 'app-flashcard-deck-view-page',
  standalone: true,
  imports: [RouterModule, CommonModule, FlashcardEditContainerComponent, FlashcardReviewContainerComponent],
  templateUrl: './flashcard-deck-edit-page.component.html',
  styleUrl: './flashcard-deck-edit-page.component.css'
})
export class FlashcardDeckEditPageComponent implements OnInit {
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

  saveChanges() {
    const flashcardDeckChanges = this.flashcardDeck?.getAllChanges();
    if (flashcardDeckChanges) {
      this.service.updateFlashcardDeck(flashcardDeckChanges).subscribe(data => {
        console.log("Flashcard deck updated");
        console.log(data);
      });
    }

    const updatePromises = this.flashcardDeck?.flashcards.map(async flashcard => {
      const changes = flashcard.getAllChanges();
      if (!changes) return;
      const data = await this.service.updateFlashcard(flashcard).toPromise();
      console.log("Flashcard updated");
      console.log(data);
    }) ?? [];

    Promise.all(updatePromises).then(() => {
      this.router.navigate([`/flashcard-deck/${this.flashcardDeck!.id}`]);
    });
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
}

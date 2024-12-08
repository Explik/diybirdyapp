import { Component, OnInit } from '@angular/core';
import { FlashcardContent } from '../../../../shared/models/content.interface';
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { RecognizabilityRatingComponent } from "../../../../shared/components/recognizability-rating/recognizability-rating.component";
import { FlashcardReviewComponent } from '../../../import/components/flashcard-review/flashcard-review.component';

@Component({
  selector: 'app-exercise-review-flashcard-content-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, FlashcardReviewComponent, RecognizabilityRatingComponent],
  templateUrl: './exercise-review-flashcard-content-container.component.html',
  styleUrl: './exercise-review-flashcard-content-container.component.css'
})
export class ExerciseReviewFlashcardContentContainerComponent implements OnInit {
  content?: FlashcardContent;

  constructor(private service: ExerciseContentService) { }

  ngOnInit(): void {
    this.service.getContent<FlashcardContent>().subscribe(data => this.content = data);
  }

  onRatingSelected(rating: string) {
    console.log(rating);
    this.service.submitAnswer({ type: "recognizability-rating", rating });
  }
}

import { Component, OnInit } from '@angular/core';
import { FlashcardContent } from '../../../../shared/models/content.interface';
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { GenericFlashcardComponent } from "../../../import/components/generic-flashcard/generic-flashcard.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-exercise-review-flashcard-content-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, GenericFlashcardComponent],
  templateUrl: './exercise-review-flashcard-content-container.component.html',
  styleUrl: './exercise-review-flashcard-content-container.component.css'
})
export class ExerciseReviewFlashcardContentContainerComponent implements OnInit {
  content?: FlashcardContent;

  constructor(private service: ExerciseContentService) { }

  ngOnInit(): void {
    this.service.getContent<FlashcardContent>("flashcard-content").subscribe(data => this.content = data);
  }
}

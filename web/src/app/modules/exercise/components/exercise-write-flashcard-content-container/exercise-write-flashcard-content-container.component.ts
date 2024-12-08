import { Component, OnInit } from '@angular/core';
import { FlashcardContent } from '../../../../shared/models/content.interface';
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { FlashcardEditComponent } from "../../../flashcard/components/flashcard-edit/flashcard-edit.component";
import { CommonModule } from '@angular/common';
import { RecognizabilityRatingComponent } from "../../../../shared/components/recognizability-rating/recognizability-rating.component";

@Component({
  selector: 'app-exercise-write-flashcard-content-container',
  standalone: true,
  imports: [],
  templateUrl: './exercise-write-flashcard-content-container.component.html',
  styleUrl: './exercise-write-flashcard-content-container.component.css'
})
export class ExerciseWriteFlashcardContentContainerComponent implements OnInit {
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

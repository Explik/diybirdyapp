import { Component, OnInit } from '@angular/core';
import { FlashcardContent } from '../../../../shared/models/content.interface';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { FlashcardReviewComponent } from '../../../flashcard/components/flashcard-review/flashcard-review.component';
import { ExerciseService } from '../../services/exercise.service';
import { CorrectableMultipleChoiceTextFieldComponent } from '../../../../shared/components/correctable-multiple-choice-text-field/correctable-multiple-choice-text-field.component';

@Component({
  selector: 'app-exercise-content-select-flashcard-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, FlashcardReviewComponent, CorrectableMultipleChoiceTextFieldComponent],
  templateUrl: './exercise-content-select-flashcard-container.component.html'
})
export class ExerciseContentSelectFlashcardContainerComponent implements OnInit {
  content?: FlashcardContent;
  input?: ExerciseInputMultipleChoiceTextDto;

  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getContent<FlashcardContent>().subscribe(data => this.content = data);
    this.service.getInput<ExerciseInputMultipleChoiceTextDto>().subscribe(data => this.input = data);
  }

  handleOptionSelected(optionId: string): void {
    this.service.submitAnswerAsync({ type: "multiple-choice", value: optionId });
  }
}

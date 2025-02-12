import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseInputSelectOptionsComponent } from '../exercise-input-select-options/exercise-input-select-options.component';
import { DynamicFlashcardContentComponent } from "../dynamic-flashcard-content/dynamic-flashcard-content.component";
import { ExerciseContentFlashcardDto, ExerciseInputMultipleChoiceTextDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-content-select-flashcard-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, ExerciseInputSelectOptionsComponent, DynamicFlashcardContentComponent],
  templateUrl: './exercise-content-select-flashcard-container.component.html'
})
export class ExerciseContentSelectFlashcardContainerComponent implements OnInit {
  content?: ExerciseContentFlashcardDto;
  input?: ExerciseInputMultipleChoiceTextDto;

  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getContent<ExerciseContentFlashcardDto>().subscribe(data => this.content = data);
    this.service.getInput<ExerciseInputMultipleChoiceTextDto>().subscribe(data => this.input = data);
  }

  handleOptionSelected(optionId: string): void {
    this.service.submitAnswerAsync({ type: "multiple-choice", value: optionId });
  }
}

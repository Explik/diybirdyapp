import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from "../../components/instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseInputSelectOptionsComponent } from '../../components/exercise-input-select-options/exercise-input-select-options.component';
import { DynamicFlashcardContentComponent } from "../../components/dynamic-flashcard-content/dynamic-flashcard-content.component";
import { ExerciseContentFlashcardDto, ExerciseInputSelectOptionsDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-content-select-flashcard-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, ExerciseInputSelectOptionsComponent, DynamicFlashcardContentComponent],
  templateUrl: './exercise-content-select-flashcard-container.component.html'
})
export class ExerciseContentSelectFlashcardContainerComponent implements OnInit {
  content?: ExerciseContentFlashcardDto;
  input?: ExerciseInputSelectOptionsDto;

  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getContent<ExerciseContentFlashcardDto>().subscribe(data => this.content = data);
    this.service.getInput<ExerciseInputSelectOptionsDto>().subscribe(data => this.input = data);
  }

  handleOptionSelected(optionId: string): void {
    this.service.submitAnswerAsync({ type: "multiple-choice", value: optionId });
  }
}

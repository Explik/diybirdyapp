import { Component, OnInit } from '@angular/core';
import { GenericExercise } from '../../models/exercise.interface';
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { CorrectableMultipleChoiceTextFieldComponent } from "../../../../shared/components/correctable-multiple-choice-text-field/correctable-multiple-choice-text-field.component";
import { MultipleChoiceTextInput } from '../../../../shared/models/input.interface';
import { CommonModule } from '@angular/common';
//import { CorrectableMultipleChoiceTextInput } from '../../../../shared/models/input.interface';

@Component({
  selector: 'app-exercise-multiple-text-choice-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, CorrectableMultipleChoiceTextFieldComponent],
  templateUrl: './exercise-multiple-text-choice-container.component.html',
  styleUrl: './exercise-multiple-text-choice-container.component.css'
})
export class ExerciseMultipleTextChoiceContainerComponent implements OnInit {
  input?: MultipleChoiceTextInput;

  constructor(private service: ExerciseContentService) { }

  ngOnInit(): void {
    this.service.getInput<MultipleChoiceTextInput>("multiple-choice-text-input").subscribe(data => this.input = data);
  }

  handleCheckAnswer() {
    return this.service.submitAnswer({
      id: '',
      exerciseId: ''
    });
  }
}

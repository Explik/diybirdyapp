import { Component, OnInit } from '@angular/core';
import { GenericExercise } from '../../models/exercise.interface';
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { CorrectableMultipleChoiceFieldComponent } from "../../../../shared/components/correctable-multiple-choice-field/correctable-multiple-choice-field.component";
import { CorrectableMultipleChoiceTextInput } from '../../../../shared/models/input.interface';

@Component({
  selector: 'app-exercise-multiple-text-choice-container',
  standalone: true,
  imports: [InstructionComponent, CorrectableMultipleChoiceFieldComponent],
  templateUrl: './exercise-multiple-text-choice-container.component.html',
  styleUrl: './exercise-multiple-text-choice-container.component.css'
})
export class ExerciseMultipleTextChoiceContainerComponent implements OnInit {
  input?: CorrectableMultipleChoiceTextInput;

  constructor(private service: ExerciseContentService) { }

  ngOnInit(): void {
    this.service.getInput<CorrectableMultipleChoiceTextInput>("multiple-choice-text-input").subscribe(data => this.input = data);
  }

  handleCheckAnswer() {
    return this.service.submitAnswer({
      id: '',
      exerciseId: ''
    });
  }
}

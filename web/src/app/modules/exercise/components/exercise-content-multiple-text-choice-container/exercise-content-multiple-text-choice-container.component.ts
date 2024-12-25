import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from "../instruction/instruction.component";
import { CorrectableMultipleChoiceTextFieldComponent } from "../../../../shared/components/correctable-multiple-choice-text-field/correctable-multiple-choice-text-field.component";
import { CommonModule } from '@angular/common';
import { ExerciseService } from '../../services/exercise.service';
//import { CorrectableMultipleChoiceTextInput } from '../../../../shared/models/input.interface';

@Component({
  selector: 'app-exercise-content-multiple-text-choice-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, CorrectableMultipleChoiceTextFieldComponent],
  templateUrl: './exercise-content-multiple-text-choice-container.component.html'
})
export class ExerciseContentMultipleTextChoiceContainerComponent implements OnInit {
  input?: ExerciseInputMultipleChoiceTextDto;

  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getInput<ExerciseInputMultipleChoiceTextDto>().subscribe(data => this.input = data);
  }
}

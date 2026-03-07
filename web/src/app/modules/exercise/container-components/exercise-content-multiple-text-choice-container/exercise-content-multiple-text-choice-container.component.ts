import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from "../../components/instruction/instruction.component";
import { ExerciseInputSelectOptionsComponent } from "../../components/exercise-input-select-options/exercise-input-select-options.component";

import { ExerciseService } from '../../services/exercise.service';
import { ExerciseInputSelectOptionsDto } from '../../../../shared/api-client';
//import { CorrectableMultipleChoiceTextInput } from '../../../../shared/models/input.interface';

@Component({
  selector: 'app-exercise-content-multiple-text-choice-container',
  standalone: true,
  imports: [InstructionComponent, ExerciseInputSelectOptionsComponent],
  templateUrl: './exercise-content-multiple-text-choice-container.component.html'
})
export class ExerciseContentMultipleTextChoiceContainerComponent implements OnInit {
  input?: ExerciseInputSelectOptionsDto;

  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getInput<ExerciseInputSelectOptionsDto>().subscribe(data => this.input = data);
  }
}

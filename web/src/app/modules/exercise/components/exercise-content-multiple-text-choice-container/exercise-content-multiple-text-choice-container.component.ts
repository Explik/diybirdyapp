import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from "../instruction/instruction.component";
import { ExerciseInputSelectOptionsComponent } from "../exercise-input-select-options/exercise-input-select-options.component";
import { CommonModule } from '@angular/common';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseInputMultipleChoiceTextDto } from '../../../../shared/api-client';
//import { CorrectableMultipleChoiceTextInput } from '../../../../shared/models/input.interface';

@Component({
  selector: 'app-exercise-content-multiple-text-choice-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, ExerciseInputSelectOptionsComponent],
  templateUrl: './exercise-content-multiple-text-choice-container.component.html'
})
export class ExerciseContentMultipleTextChoiceContainerComponent implements OnInit {
  input?: ExerciseInputMultipleChoiceTextDto;

  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getInput<ExerciseInputMultipleChoiceTextDto>().subscribe(data => this.input = data);
  }
}

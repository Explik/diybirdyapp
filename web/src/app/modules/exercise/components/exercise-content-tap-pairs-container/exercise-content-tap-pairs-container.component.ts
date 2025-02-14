import { Component } from '@angular/core';
import { InstructionComponent } from "../instruction/instruction.component";
import { ExerciseInputPairOptionsComponent } from "../exercise-input-pair-options/exercise-input-pair-options.component";
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseInputPairOptionsDto } from '../../../../shared/api-client';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-exercise-content-tap-pairs-container',
  imports: [CommonModule, InstructionComponent, ExerciseInputPairOptionsComponent],
  templateUrl: './exercise-content-tap-pairs-container.component.html',
})
export class ExerciseContentTapPairsContainerComponent {
  input?: ExerciseInputPairOptionsDto;

  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getInput<ExerciseInputPairOptionsDto>().subscribe(data => this.input = data);
  }
}

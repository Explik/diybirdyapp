import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ExerciseContentDto, ExerciseInputArrangeTextOptionsDto } from '../../../../shared/api-client';
import { ExerciseService } from '../../services/exercise.service';
import { InstructionComponent } from "../../components/instruction/instruction.component";
import { ExerciseInputArrangeTextOptionsComponent } from "../../components/exercise-input-arrange-text-options/exercise-input-arrange-text-options.component";
import { DynamicContentComponent } from "../../components/dynamic-content/dynamic-content.component";

@Component({
  selector: 'app-exercise-content-arrange-words-in-translation',
  imports: [CommonModule, InstructionComponent, ExerciseInputArrangeTextOptionsComponent, DynamicContentComponent],
  templateUrl: './exercise-content-arrange-words-in-translation.component.html'
})
export class ExerciseContentArrangeWordsInTranslationComponent {
  content?: ExerciseContentDto; 
  input?: ExerciseInputArrangeTextOptionsDto; 

  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getContent<ExerciseContentDto>().subscribe(data => this.content = data);
    this.service.getInput<ExerciseInputArrangeTextOptionsDto>().subscribe(data => this.input = data);
  }
}

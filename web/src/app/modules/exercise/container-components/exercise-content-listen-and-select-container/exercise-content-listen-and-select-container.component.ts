import { Component } from '@angular/core';
import { ExerciseContentAudioDto, ExerciseInputSelectOptionsDto } from '../../../../shared/api-client';
import { ExerciseService } from '../../services/exercise.service';
import { InstructionComponent } from "../../components/instruction/instruction.component";
import { ExerciseInputSelectOptionsComponent } from "../../components/exercise-input-select-options/exercise-input-select-options.component";
import { DynamicAudioContentComponent } from "../../components/dynamic-audio-content/dynamic-audio-content.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-exercise-content-listen-and-select-container',
  imports: [CommonModule, InstructionComponent, ExerciseInputSelectOptionsComponent, DynamicAudioContentComponent],
  templateUrl: './exercise-content-listen-and-select-container.component.html'
})
export class ExerciseContentListenAndSelectContainerComponent {
  content?: ExerciseContentAudioDto;  
  input?: ExerciseInputSelectOptionsDto;
    
  constructor(private service: ExerciseService) { }

  ngOnInit(): void {
    this.service.getContent<ExerciseContentAudioDto>().subscribe(data => this.content = data);
    this.service.getInput<ExerciseInputSelectOptionsDto>().subscribe(data => this.input = data);
  }

  handleOptionSelected(optionId: string): void {
    this.service.submitAnswerAsync({ type: "multiple-choice", value: optionId });
  }
}

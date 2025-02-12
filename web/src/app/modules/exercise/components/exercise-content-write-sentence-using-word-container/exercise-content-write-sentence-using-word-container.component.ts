import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { ExerciseInputWriteTextComponent } from "../exercise-input-write-text/exercise-input-write-text.component";
import { DefaultContentService } from '../../services/defaultContent.service';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseInputTextDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-content-write-sentence-using-word-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, ExerciseInputWriteTextComponent],
  templateUrl: './exercise-content-write-sentence-using-word-container.component.html'
})
export class ExerciseContentWriteSentenceUsingWordContainerComponent implements OnInit {
  word?: string;
  input?: ExerciseInputTextDto;

  constructor(
    private exerciseService: ExerciseService,
    private defaultContentService : DefaultContentService) { }
  
  ngOnInit(): void {
    this.exerciseService.getProperty("word").subscribe(data => { 
      this.word = data 
    });
    
    this.exerciseService.setDefaultInput(this.defaultContentService.getTextInput());
    this.exerciseService.getInput<ExerciseInputTextDto>().subscribe(data => this.input = data);
  }
}

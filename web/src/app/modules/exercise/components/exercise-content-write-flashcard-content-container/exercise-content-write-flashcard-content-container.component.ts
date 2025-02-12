import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { ExerciseInputWriteTextComponent } from '../exercise-input-write-text/exercise-input-write-text.component';
import { DefaultContentService } from '../../services/defaultContent.service';
import { ExerciseService } from '../../services/exercise.service';
import { DynamicFlashcardContentComponent } from "../dynamic-flashcard-content/dynamic-flashcard-content.component";
import { ExerciseContentFlashcardDto, ExerciseInputTextDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-content-write-flashcard-content-container',
  standalone: true,
  imports: [InstructionComponent, CommonModule, ExerciseInputWriteTextComponent, DynamicFlashcardContentComponent],
  templateUrl: './exercise-content-write-flashcard-content-container.component.html'
})
export class ExerciseContentWriteFlashcardContentContainerComponent implements OnInit {
  content?: ExerciseContentFlashcardDto;
  input?: ExerciseInputTextDto;

  constructor(
    private contentService: ExerciseService,
    private defaultContentService: DefaultContentService
  ) { }

  ngOnInit(): void {
    this.contentService.getContent<ExerciseContentFlashcardDto>().subscribe(data => this.content = data);
    
    this.contentService.setDefaultInput(this.defaultContentService.getTextInput());
    this.contentService.getInput<ExerciseInputTextDto>().subscribe(data => { 
      this.input = data; 
    });
  }
}

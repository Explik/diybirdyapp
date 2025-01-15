import { Component, OnInit } from '@angular/core';
import { FlashcardContent } from '../../../../shared/models/content.interface';
import { InstructionComponent } from "../instruction/instruction.component";
import { FlashcardEditComponent } from "../../../flashcard/components/flashcard-edit/flashcard-edit.component";
import { CommonModule } from '@angular/common';
import { RecognizabilityRatingComponent } from "../../../../shared/components/recognizability-rating/recognizability-rating.component";
import { CorrectableTextFieldComponent } from '../../../../shared/components/correctable-text-field/correctable-text-field.component';
import { DefaultContentService } from '../../services/defaultContent.service';
import { ExerciseService } from '../../services/exercise.service';
import { DynamicFlashcardContentComponent } from "../dynamic-flashcard-content/dynamic-flashcard-content.component";

@Component({
  selector: 'app-exercise-content-write-flashcard-content-container',
  standalone: true,
  imports: [InstructionComponent, CommonModule, CorrectableTextFieldComponent, DynamicFlashcardContentComponent],
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

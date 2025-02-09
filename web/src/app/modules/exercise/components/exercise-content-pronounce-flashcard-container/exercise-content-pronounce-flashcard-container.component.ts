import { Component } from '@angular/core';
import { FlashcardContent } from '../../../../shared/models/content.interface';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseSessionService } from '../../services/exerciseSession.service';
import { DefaultContentService } from '../../services/defaultContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { CorrectableAudioFieldComponent } from "../../../../shared/components/correctable-audio-field/correctable-audio-field.component";
import { DynamicFlashcardContentComponent } from "../dynamic-flashcard-content/dynamic-flashcard-content.component";

@Component({
  selector: 'app-exercise-content-pronounce-flashcard-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, CorrectableAudioFieldComponent, DynamicFlashcardContentComponent],
  templateUrl: './exercise-content-pronounce-flashcard-container.component.html'
})
export class ExerciseContentPronounceFlashcardContainerComponent {
  content?: ExerciseContentFlashcardDto;
  input?: ExerciseInputAudioDto;

  constructor(
    private exerciseService: ExerciseService,
    private defaultContentService: DefaultContentService
  ) { }

  ngOnInit(): void {
    this.exerciseService.getContent<ExerciseContentFlashcardDto>().subscribe(data => this.content = data);
    this.exerciseService.setDefaultInput(this.defaultContentService.getAudioInput());
    this.exerciseService.getInput<ExerciseInputAudioDto>().subscribe(data => { 
      this.input = data; 
    });
  }  
}

import { Component, ChangeDetectorRef } from '@angular/core';
import { FlashcardContent } from '../../../../shared/models/content.interface';
import { ExerciseService } from '../../services/exercise.service';
import { DefaultContentService } from '../../services/defaultContent.service';
import { InstructionComponent } from "../../components/instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { ExerciseInputRecordAudioComponent } from "../../components/exercise-input-record-audio/exercise-input-record-audio.component";
import { DynamicFlashcardContentComponent } from "../../components/dynamic-flashcard-content/dynamic-flashcard-content.component";
import { ExerciseContentFlashcardDto, ExerciseInputRecordAudioDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-content-pronounce-flashcard-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, ExerciseInputRecordAudioComponent, DynamicFlashcardContentComponent],
  templateUrl: './exercise-content-pronounce-flashcard-container.component.html'
})
export class ExerciseContentPronounceFlashcardContainerComponent {
  content?: ExerciseContentFlashcardDto;
  input?: ExerciseInputRecordAudioDto;

  constructor(
    private exerciseService: ExerciseService,
    private defaultContentService: DefaultContentService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.exerciseService.getContent<ExerciseContentFlashcardDto>().subscribe(data => this.content = data);
    this.exerciseService.setDefaultInput(this.defaultContentService.getAudioInput());
    this.exerciseService.getInput<ExerciseInputRecordAudioDto>().subscribe(data => { 
      this.input = data;
      this.cdr.markForCheck();
    });
  }  

  handleRecordingFinished(): void {
    if (!this.input?.url) 
      throw new Error('Input.url should be defined after recording is finished');

    this.exerciseService.checkAnswerAsync(); 
  }
}

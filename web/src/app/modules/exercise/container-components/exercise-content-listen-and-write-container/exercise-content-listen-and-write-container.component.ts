import { Component } from '@angular/core';
import { ExerciseContentAudioDto, ExerciseContentFlashcardDto, ExerciseInputWriteTextDto } from '../../../../shared/api-client';
import { ExerciseService } from '../../services/exercise.service';
import { DefaultContentService } from '../../services/defaultContent.service';
import { InstructionComponent } from "../../components/instruction/instruction.component";
import { DynamicAudioContentComponent } from "../../components/dynamic-audio-content/dynamic-audio-content.component";
import { ExerciseInputWriteTextComponent } from "../../components/exercise-input-write-text/exercise-input-write-text.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-exercise-content-listen-and-write-container',
  imports: [CommonModule, InstructionComponent, DynamicAudioContentComponent, ExerciseInputWriteTextComponent],
  templateUrl: './exercise-content-listen-and-write-container.component.html'
})
export class ExerciseContentListenAndWriteContainerComponent {
    content?: ExerciseContentAudioDto;
    input?: ExerciseInputWriteTextDto;
  
    constructor(
      private contentService: ExerciseService,
      private defaultContentService: DefaultContentService
    ) { }
  
    ngOnInit(): void {
      this.contentService.getContent<ExerciseContentAudioDto>().subscribe(data => this.content = data);
      
      this.contentService.setDefaultInput(this.defaultContentService.getTextInput());
      this.contentService.getInput<ExerciseInputWriteTextDto>().subscribe(data => { 
        this.input = data; 
      });
    }
}

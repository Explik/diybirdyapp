import { Component, inject, OnInit } from '@angular/core';
import { InstructionComponent } from "../../components/instruction/instruction.component";
import { TextQuoteComponent } from "../../../../shared/components/text-quote/text-quote.component";
import { ExerciseInputWriteTextComponent } from "../../components/exercise-input-write-text/exercise-input-write-text.component";
import { TextContent } from '../../../../shared/models/content.interface';
import { CommonModule } from '@angular/common';
import { DefaultContentService } from '../../services/defaultContent.service';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseInputWriteTextDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-content-write-translated-sentence-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, TextQuoteComponent, ExerciseInputWriteTextComponent],
  templateUrl: './exercise-content-write-translated-sentence-container.component.html'
})
export class ExerciseContentWriteTranslatedSentenceContainerComponent implements OnInit {
  targetLanguage?: string;
  content?: TextContent;
  input?: ExerciseInputWriteTextDto;

  constructor(
    private exerciseService: ExerciseService,
    private defaultContentService: DefaultContentService) { }

  ngOnInit(): void {
    this.exerciseService.getProperty("targetLanguage").subscribe(data => this.targetLanguage = data);
    this.exerciseService.getContent<TextContent>().subscribe(data => this.content = data);

    this.exerciseService.setDefaultInput(this.defaultContentService.getTextInput());
    this.exerciseService.getInput<ExerciseInputWriteTextDto>().subscribe(data => this.input = data);
  }
}

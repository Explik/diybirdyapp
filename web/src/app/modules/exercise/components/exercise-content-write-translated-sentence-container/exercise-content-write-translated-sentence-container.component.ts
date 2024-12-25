import { Component, inject, OnInit } from '@angular/core';
import { InstructionComponent } from "../instruction/instruction.component";
import { TextQuoteComponent } from "../../../../shared/components/text-quote/text-quote.component";
import { CorrectableTextFieldComponent } from "../../../../shared/components/correctable-text-field/correctable-text-field.component";
import { TextContent } from '../../../../shared/models/content.interface';
import { TextInput, TextInputFeedback } from '../../../../shared/models/input.interface';
import { CommonModule } from '@angular/common';
import { DefaultContentService } from '../../services/defaultContent.service';
import { ExerciseService } from '../../services/exercise.service';

@Component({
  selector: 'app-exercise-content-write-translated-sentence-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, TextQuoteComponent, CorrectableTextFieldComponent],
  templateUrl: './exercise-content-write-translated-sentence-container.component.html'
})
export class ExerciseContentWriteTranslatedSentenceContainerComponent implements OnInit {
  targetLanguage?: string;
  content?: TextContent;
  input?: TextInput;
  inputFeedback?: TextInputFeedback;

  constructor(
    private exerciseService: ExerciseService,
    private defaultContentService: DefaultContentService) { }

  ngOnInit(): void {
    this.exerciseService.getProperty("targetLanguage").subscribe(data => this.targetLanguage = data);
    this.exerciseService.getContent<TextContent>().subscribe(data => this.content = data);
    this.exerciseService.getInput<TextInput>().subscribe(data => this.input = data ?? this.defaultContentService.getTextInput());
  }
}

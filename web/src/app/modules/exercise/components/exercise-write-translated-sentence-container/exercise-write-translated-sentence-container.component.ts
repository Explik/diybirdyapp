import { Component, inject, OnInit } from '@angular/core';
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { TextQuoteComponent } from "../../../../shared/components/text-quote/text-quote.component";
import { CorrectableTextFieldComponent } from "../../../../shared/components/correctable-text-field/correctable-text-field.component";
import { TextContent } from '../../../../shared/models/content.interface';
import { TextInput, TextInputFeedback } from '../../../../shared/models/input.interface';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-exercise-write-translated-sentence-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, TextQuoteComponent, CorrectableTextFieldComponent],
  templateUrl: './exercise-write-translated-sentence-container.component.html',
  styleUrl: './exercise-write-translated-sentence-container.component.css'
})
export class ExerciseWriteTranslatedSentenceContainerComponent implements OnInit {
  targetLanguage?: string;
  content?: TextContent;
  input?: TextInput;
  inputFeedback?: TextInputFeedback;

  constructor(private service: ExerciseContentService) { }

  ngOnInit(): void {
    this.service.getProperty("targetLanguage").subscribe(data => this.targetLanguage = data);
    this.service.getContent<TextContent>("text-content").subscribe(data => this.content = data);
    this.service.getInput<TextInput>("text-input").subscribe(data => this.input = data);
    this.service.getInputFeedback<TextInputFeedback>("text-input-feedback").subscribe(data => this.inputFeedback = data);
  }

  handleCheckAnswer() {
    return this.service.submitAnswer({
      id: '',
      exerciseId: ''
    });
  }
}

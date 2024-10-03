import { Component, OnInit } from '@angular/core';
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { CorrectableTextFieldComponent } from "../../../../shared/components/correctable-text-field/correctable-text-field.component";
import { GenericExercise } from '../../models/exercise.interface';
import { TextInput } from '../../../../shared/models/input.interface';
import { DefaultContentService } from '../../services/defaultContent.service';

@Component({
  selector: 'app-exercise-write-sentence-using-word-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, CorrectableTextFieldComponent],
  templateUrl: './exercise-write-sentence-using-word-container.component.html',
  styleUrl: './exercise-write-sentence-using-word-container.component.css'
})
export class ExerciseWriteSentenceUsingWordContainerComponent implements OnInit {
  word?: string;
  input?: TextInput;

  constructor(
    private contentService: ExerciseContentService,
    private defaultContentService : DefaultContentService) { }
  
  ngOnInit(): void {
    this.contentService.getProperty("word").subscribe(data => { 
      this.word = data 
    });
    
    this.contentService.getInput<TextInput>().subscribe(data => { 
      this.input = data ?? this.defaultContentService.getTextInput()
    });
  }

  handleCheckAnswer() {
    return this.contentService.submitAnswer({
      id: '',
      exerciseId: ''
    });
  }
}

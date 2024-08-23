import { Component, OnInit } from '@angular/core';
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { InstructionComponent } from "../instruction/instruction.component";
import { CommonModule } from '@angular/common';
import { CorrectableTextFieldComponent } from "../../../../shared/components/correctable-text-field/correctable-text-field.component";
import { GenericExercise } from '../../models/exercise.interface';
import { CorrectableTextInput, TextInput } from '../../../../shared/models/input.interface';

@Component({
  selector: 'app-exercise-write-sentence-using-word-container',
  standalone: true,
  imports: [CommonModule, InstructionComponent, CorrectableTextFieldComponent],
  templateUrl: './exercise-write-sentence-using-word-container.component.html',
  styleUrl: './exercise-write-sentence-using-word-container.component.css'
})
export class ExerciseWriteSentenceUsingWordContainerComponent implements OnInit {
  word?: string;
  input?: CorrectableTextInput;

  constructor(private service: ExerciseContentService) { }
  
  ngOnInit(): void {
    this.service.getProperty("word").subscribe(data => this.word = data);
    this.service.getInput<CorrectableTextInput>("text-input").subscribe(data => this.input = data);
  }

  handleCheckAnswer() {
    return this.service.submitAnswer({
      id: '',
      exerciseId: ''
    });
  }
}

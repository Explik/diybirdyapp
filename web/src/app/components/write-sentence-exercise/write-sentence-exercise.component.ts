import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BaseExercise, WriteSentenceExercise } from '../../interfaces/exercise.interface';

@Component({
  selector: 'write-sentence-exercise',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './write-sentence-exercise.component.html',
  styleUrl: './write-sentence-exercise.component.css'
})
export class WriteSentenceExerciseComponent implements OnInit {
  @Input() data: WriteSentenceExercise | undefined

  word: string = '';
  userInput: string = '';
  message: string = '';

  ngOnInit(): void {
    if (!this.data)
      return; 

    this.word = this.data.word
  }

  checkSentence(): void {
    if (this.userInput.includes(this.word)) {
      this.message = 'Correct!';
    } else {
      this.message = `The sentence must include the word: ${this.word}`;
    }
  }

  resetExercise(): void {
    this.userInput = '';
    this.message = '';
  }
}

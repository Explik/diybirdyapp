import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BaseExercise, WriteSentenceUsingWordExercise, WrittenExerciseAnswer } from '../../interfaces/exercise.interface';
import { ExerciseService } from '../../services/exercise.service';

@Component({
  selector: 'write-sentence-exercise',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './write-sentence-using-word-exercise.component.html',
  styleUrl: './write-sentence-using-word-exercise.component.css'
})
export class WriteSentenceExerciseUsingWordComponent implements OnInit {
  @Input() data: WriteSentenceUsingWordExercise | undefined

  word: string = '';
  userInput: string = '';
  message: string = '';

  constructor(
    private exerciseService: ExerciseService,
  ) {}

  ngOnInit(): void {
    if (!this.data)
      return; 

    this.word = this.data.word
  }

  checkSentence(): void {
    if (!this.data)
      return; 
    
    const exercise: WriteSentenceUsingWordExercise = {
      ...this.data,
      exerciseAnswer: { id: this.getRandomInt(1000) + "", answerType: "written-answer", text: this.userInput }
    };
    this.exerciseService.submitExerciseAnswer(this.data.id, exercise).subscribe();

    if (this.userInput.includes(this.word)) {
      this.message = 'Correct!';
    } else {
      this.message = `The sentence must include the word: ${this.word}`;
    }
  }

  getRandomInt(max: number) {
    return Math.floor(Math.random() * max);
  }  

  resetExercise(): void {
    this.userInput = '';
    this.message = '';
  }
}

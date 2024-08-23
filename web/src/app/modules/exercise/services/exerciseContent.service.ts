import { Inject, Injectable } from '@angular/core';
import { Observable, BehaviorSubject, of, map } from 'rxjs';
import { Exercise, ExerciseAnswer, GenericExercise, GenericExerciseContent, GenericExerciseInput } from '../models/exercise.interface';
import { TextContent } from '../../../shared/models/content.interface';
import { CorrectableMultipleChoiceTextInput, CorrectableTextInput } from '../../../shared/models/input.interface';
import { ExerciseService } from './exercise.service';

@Injectable({
  providedIn: 'root'
})
export class ExerciseContentService {
  constructor(private service: ExerciseService) { }

  getProperty(name: string): Observable<string> {
    return this.service
      .getExercise()
      .pipe(map(data => (data as any)[name] ?? name));
  }

  getContent<T>(identifier: string): Observable<T> {
    if (identifier === "text-content") {
      const textContent: TextContent = {
        text: "This is text content"
      };
      return of(textContent as T)
    }
    throw new Error("Unsupported content " + identifier);
  }

  getInput<T>(identifier: string): Observable<T> {
    if (identifier === "text-input") {
      const textInput: CorrectableTextInput = {
        text: "Initial value"
      }
      return of(textInput as T);
    }
    if (identifier === "multiple-choice-text-input") {
      const multipleChoiceInput: CorrectableMultipleChoiceTextInput = {
        options: {
          "1": "A dog",
          "2": "A cat",
          "3": "An apple",
          "4": "An orange"
        }
      }
      return of(multipleChoiceInput as T);
    }
    throw new Error("Unsupported content " + identifier);
  }

  submitAnswer(answer: ExerciseAnswer) {
    
  }
}
import { Inject, Injectable } from '@angular/core';
import { Observable, BehaviorSubject, of, map, switchMap } from 'rxjs';
import { Exercise, ExerciseAnswer, GenericExercise, GenericExerciseContent, GenericExerciseInput } from '../models/exercise.interface';
import { TextContent } from '../../../shared/models/content.interface';
import { ExerciseService } from './exercise.service';
import { TextInput } from '../../../shared/models/input.interface';
import { DefaultContentService } from './defaultContent.service';

@Injectable({
  providedIn: 'root'
})
export class ExerciseContentService {
  constructor(private service: ExerciseService) { }

  getProperty(name: string): Observable<string> {
    return this.service
      .getExercise()
      .pipe(map(data => (data?.properties as any)[name] ?? name));
  }

  getContent<T>(): Observable<T|undefined> {
      return this.service
        .getExercise()
        .pipe(map(data => <T>data?.content))
  }

  getInput<T>(): Observable<T | undefined> {
    return this.service
      .getExercise()
      .pipe(map(data => data?.input as T));
  }

  getInputFeedback<T>(identifier: string): Observable<T|undefined> {
    return of(undefined);
  }

  submitAnswer(answer: ExerciseAnswer) {

  }
}

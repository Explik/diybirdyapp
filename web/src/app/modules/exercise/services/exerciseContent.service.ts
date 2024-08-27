import { Inject, Injectable } from '@angular/core';
import { Observable, BehaviorSubject, of, map, switchMap } from 'rxjs';
import { Exercise, ExerciseAnswer, GenericExercise, GenericExerciseContent, GenericExerciseInput } from '../models/exercise.interface';
import { TextContent } from '../../../shared/models/content.interface';
import { ExerciseService } from './exercise.service';
import { TextInput } from '../../../shared/models/input.interface';
import { ExerciseDefaultContentService } from './exerciseDefaultContent.service';

@Injectable({
  providedIn: 'root'
})
export class ExerciseContentService {
  constructor(
    private service: ExerciseService,
    private defaultContentService: ExerciseDefaultContentService) { }

  getProperty(name: string): Observable<string> {
    return this.service
      .getExercise()
      .pipe(map(data => (data?.properties as any)[name] ?? name));
  }

  getContent<T>(identifier: string): Observable<T> {
      return this.service
        .getExercise()
        .pipe(map(data => <T>data?.content))
  }

  getInput<T>(identifier: string): Observable<T> {
    return this.service
      .getExercise()
      .pipe(switchMap(
        data => data?.input ? of(data.input as T) : this.defaultContentService.getInput<T>(identifier)
      ));
  }

  getInputFeedback<T>(identifier: string): Observable<T|undefined> {
    return of(undefined);
  }

  submitAnswer(answer: ExerciseAnswer) {

  }
}

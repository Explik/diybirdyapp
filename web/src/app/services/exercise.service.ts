import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { from, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BaseExercise, BaseExerciseAnswer, WriteSentenceUsingWordExercise } from '../interfaces/exercise.interface';

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {
  private baseUrl = `${environment.apiUrl}/exercise`;  // Replace with your actual API URL

  constructor(private http: HttpClient) { }

  createExercise<T>(exercise: T): Observable<T> {
    return this.http.post(this.baseUrl, exercise) as Observable<T>;
  }

  getExercise(id: string): Observable<BaseExercise> {
    return this.http.get<BaseExercise>(`${this.baseUrl}/${id}`);
  }

  getExercises(): Observable<BaseExercise[]> {
    return this.http.get<BaseExercise[]>(`${this.baseUrl}`);
  }

  submitExerciseAnswer(id: string, exerciseWithAnswer: BaseExercise): Observable<any> {
    return this.http.post(`${this.baseUrl}/${id}/answer`, exerciseWithAnswer);
  }
}
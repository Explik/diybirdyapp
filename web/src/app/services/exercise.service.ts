import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { from, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BaseExercise, WriteSentenceUsingWordExercise } from '../interfaces/exercise.interface';

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {
  private baseUrl = `${environment.apiUrl}/exercise`;  // Replace with your actual API URL

  constructor(private http: HttpClient) { }

  getExercise(id: string): Observable<BaseExercise> {
    return this.http.get<BaseExercise>(`${this.baseUrl}/${id}`);
  }

  submitExerciseAnswer(id: string, userInput: string): Observable<any> {
    return from([])
    //return this.http.post(`${this.baseUrl}/${id}/answer`, { sentence: userInput });
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { from, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BaseExercise, WriteSentenceExercise } from '../interfaces/exercise.interface';

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {
  private baseUrl = `${environment.apiUrl}/exercise`;  // Replace with your actual API URL

  constructor(private http: HttpClient) { }

  getExercise(id: string): Observable<BaseExercise> {
    //const exercise : WriteSentenceExercise = { id: "1", exerciseType: "write-sentence-exercise", word: "great" };
    //return from([exercise]);
    
    return this.http.get<BaseExercise>(`${this.baseUrl}/${id}`);
  }

  submitExercise(id: string, userInput: string): Observable<any> {
    return from([])
    //return this.http.post(`${this.baseUrl}/${id}/submit`, { sentence: userInput });
  }
}
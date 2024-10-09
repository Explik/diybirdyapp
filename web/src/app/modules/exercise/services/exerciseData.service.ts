import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ExerciseAnswer } from '../models/exercise.interface';
import { TextInputFeedback } from '../../../shared/models/input.interface';

@Injectable({
    providedIn: 'root'
})
export class ExerciseDataService {
    constructor(private http: HttpClient) { }

    getExercise(id: string): Observable<ExerciseDto> {
        // TODO Add error handling
        return this.http.get<ExerciseDto>(`${environment.apiUrl}/exercise/${id}`);
    }

    getExerciseSession(id: string): Observable<ExerciseSessionDto> {
        // TODO Add error handling
        return this.http.get<ExerciseSessionDto>(`${environment.apiUrl}/exercise-session/${id}`);
    }

    getNextExercise(sessionId: string): Observable<ExerciseDto> {
        // TODO Add error handling
        return this.http.post<ExerciseDto>(`${environment.apiUrl}/exercise-session/${sessionId}/next`, {});
    }

    getExercises(): Observable<ExerciseDto[]> {
        // TODO Add error handling
        return this.http.get<ExerciseDto[]>(`${environment.apiUrl}/exercise`);
    }

    submitExerciseAnswer(exerciseId: string, answer: ExerciseAnswer): Observable<TextInputFeedback> {
        // TODO Add error handling
        return this.http.post<TextInputFeedback>(`${environment.apiUrl}/exercise/${exerciseId}/answer`, answer);
    }
}
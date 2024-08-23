import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ExerciseDataService {
    constructor(private http: HttpClient) { }

    getExercise(id: string): Observable<ExerciseDto> {
        // TODO Add error handling
        return this.http.get<ExerciseDto>(`${environment.apiUrl}/exercise/${id}`);
    }

    getExercises(): Observable<ExerciseDto[]> {
        // TODO Add error handling
        return this.http.get<ExerciseDto[]>(`${environment.apiUrl}/exercise`);
    }
}
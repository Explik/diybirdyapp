import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ExerciseAnswer } from '../models/exercise.interface';
import { ExerciseDto, ExerciseSessionDto, ExerciseSessionOptionsDto } from '../../../shared/api-client';

@Injectable({
    providedIn: 'root'
})
export class ExerciseSessionDataService {
    constructor(private http: HttpClient) { }

    // === Exercise ===
    getExercise(id: string): Observable<ExerciseDto> {
        // TODO Add error handling
        return this.http.get<ExerciseDto>(`${environment.apiUrl}/exercise/${id}`);
    }

    getExercises(): Observable<ExerciseDto[]> {
        // TODO Add error handling
        return this.http.get<ExerciseDto[]>(`${environment.apiUrl}/exercise`);
    }

    getExerciseTypes(): Observable<string[]> {
        // TODO Add error handling
        return this.http.get<string[]>(`${environment.apiUrl}/exercise/types`);
    }

    // === ExerciseAnswer ===
    submitExerciseAnswer(exerciseId: string, answer: ExerciseAnswer): Observable<ExerciseDto> {
        const formData = new FormData();
        
        // Attach files 
        if (answer.files) {
            for (let file of answer.files)
                formData.append('files', file, file.name);
        }

        // Generate JSON
        const jsonObject = { ...answer, files: undefined }; 
        const jsonString = JSON.stringify(jsonObject); 
        formData.append('answer', new Blob([jsonString], { type: 'application/json' }));

        return this.http.post<ExerciseDto>(`${environment.apiUrl}/exercise/${exerciseId}/answer/rich`, formData);
    }

    // === ExerciseSession ===
    getExerciseSession(id: string): Observable<ExerciseSessionDto> {
        // TODO Add error handling
        return this.http.get<ExerciseSessionDto>(`${environment.apiUrl}/exercise-session/${id}`);
    }

    exitExerciseSession(id: string): Observable<void> {
        // TODO Add error handling
        return this.http.post<void>(`${environment.apiUrl}/exercise-session/${id}/exit`, {});   
    }

    nextExercise(sessionId: string): Observable<ExerciseSessionDto> {
        // TODO Add error handling
        return this.http.post<ExerciseSessionDto>(`${environment.apiUrl}/exercise-session/${sessionId}/next-exercise`, {});
    }

    skipExercise(sessionId: string): Observable<ExerciseSessionDto> {
        // TODO Add error handling
        return this.http.post<ExerciseSessionDto>(`${environment.apiUrl}/exercise-session/${sessionId}/skip-exercise`, {});
    }

    getOptions(sessionId: string): Observable<ExerciseSessionOptionsDto> {
        // TODO Add error handling
        return this.http.get<any>(`${environment.apiUrl}/exercise-session/${sessionId}/options`);
    }

    applyOptions(sessionId: string, config: ExerciseSessionOptionsDto): Observable<ExerciseSessionDto> {
        // TODO Add error handling
        return this.http.post<ExerciseSessionDto>(`${environment.apiUrl}/exercise-session/${sessionId}/apply-options`, config);
    }
}
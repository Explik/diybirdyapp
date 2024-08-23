import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, skipUntil } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Exercise } from '../models/exercise.interface';

@Injectable({
    providedIn: 'root'
})
export class ExerciseService {
    private exercise$: BehaviorSubject<ExerciseDto|undefined> = new BehaviorSubject<ExerciseDto|undefined>(undefined);

    getExercise(): Observable<ExerciseDto|undefined> {
        return this.exercise$.asObservable();
    }

    setExercise(exercise: ExerciseDto) {
        this.exercise$.next(exercise);
    }
}
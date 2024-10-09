import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, skipUntil } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Exercise, ExerciseAnswer } from '../models/exercise.interface';
import { ExerciseDataService } from './exerciseData.service';
import { TextInputFeedback } from '../../../shared/models/input.interface';

@Injectable({
    providedIn: 'root'
})
export class ExerciseService {
    private exercise$: BehaviorSubject<ExerciseDto|undefined> = new BehaviorSubject<ExerciseDto|undefined>(undefined);
    private exerciseFeedback$: BehaviorSubject<TextInputFeedback|undefined> = new BehaviorSubject<TextInputFeedback|undefined>(undefined);

    constructor(private exerciseDataService: ExerciseDataService) { }

    getExercise(): Observable<ExerciseDto|undefined> {
        return this.exercise$.asObservable();
    }

    setExercise(exercise: ExerciseDto) {
        this.exercise$.next(exercise);
    }

    getExerciseFeedback(): Observable<TextInputFeedback|undefined> {
        return this.exerciseFeedback$.asObservable();
    }

    submitAnswer(answer: ExerciseAnswer) {
        const currentExercise = this.exercise$.getValue(); 
        
        if (!currentExercise) 
            throw new Error("No exercise loaded");
        
        this.exerciseDataService
            .submitExerciseAnswer(currentExercise.id, answer)
            .subscribe(x => this.exerciseFeedback$.next(x));
    }
}
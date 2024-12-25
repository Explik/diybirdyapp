import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable, of, skipUntil } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Exercise, ExerciseAnswer } from '../models/exercise.interface';
import { ExerciseSessionDataService } from './exerciseSessionData.service';
import { TextInputFeedback } from '../../../shared/models/input.interface';
import { ExerciseService } from './exercise.service';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class ExerciseSessionService {
    private session$: BehaviorSubject<ExerciseSessionDto|undefined> = new BehaviorSubject<ExerciseSessionDto|undefined>(undefined);

    constructor(
        private dataService: ExerciseSessionDataService,
        private exerciseService: ExerciseService) { }
    
    // State functions 
    loadExerciseSession(id: string) {
        this.dataService.getExerciseSession(id).subscribe(data => {
            this.setExerciseSession(data);
        });
    }

    getExerciseSession(): Observable<ExerciseSessionDto|undefined> {
        return this.session$.asObservable();
    }

    setExerciseSession(session?: ExerciseSessionDto) {
        this.session$.next(session);

        if (session) {
            this.exerciseService.setExercise(session.exercise);
        }
    }

    // Properties 
    getProgress(): Observable<number> {
        return this.session$.pipe(map(session => session?.progress?.percentage || 0));
    }

    // Actions
    async nextExerciseAsync() {
        const session = this.session$.getValue();
        if (!session) 
            throw new Error("No session found");
        
        const newSession = await this.dataService.nextExercise(session.id).toPromise();
        this.setExerciseSession(newSession);
        
        if (newSession?.exercise)
            this.exerciseService.setExercise(newSession.exercise);
    }

    async skipExerciseAsync() {
        const session = this.session$.getValue();
        if (!session) 
            throw new Error("No session found");

        const newSession = await this.dataService.skipExercise(session.id).toPromise();
        this.setExerciseSession(newSession);

        if (newSession?.exercise)
            this.exerciseService.setExercise(newSession.exercise);
    }
    
    async exitSessionAsync() {
        throw new Error("Not implemented");
    }

}
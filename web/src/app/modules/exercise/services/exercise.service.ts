import { Injectable } from "@angular/core";
import { Observable, BehaviorSubject, of, map, switchMap, lastValueFrom } from 'rxjs';
import { ExerciseAnswer, ExerciseStates } from "../models/exercise.interface";
import { ExerciseSessionDataService } from "./exerciseSessionData.service";
import { TextInputFeedback } from "../../../shared/models/input.interface";

@Injectable({
    providedIn: 'root'
})
export class ExerciseService {
    private exercise$: BehaviorSubject<ExerciseDto | undefined> = new BehaviorSubject<ExerciseDto | undefined>(undefined);
    private exerciseAnswer$: BehaviorSubject<ExerciseAnswer | undefined> = new BehaviorSubject<ExerciseAnswer | undefined>(undefined);
    private exerciseFeedback$: BehaviorSubject<TextInputFeedback | undefined> = new BehaviorSubject<TextInputFeedback | undefined>(undefined);

    constructor(private service: ExerciseSessionDataService) { }

    // State functions 
    getExercise(): Observable<ExerciseDto | undefined> {
        return this.exercise$.asObservable();
    }

    setExercise(exercise: ExerciseDto) {
        this.exercise$.next(exercise);
        this.exerciseAnswer$.next(undefined);
    }

    // Read functions
    getState(): Observable<ExerciseStates> {
        return this.exerciseAnswer$.pipe(
            map(answer => answer ? ExerciseStates.Answered : ExerciseStates.Unanswered)
        );
    }

    getProperty(name: string): Observable<string> {
        return this.getExercise()
            .pipe(map(data => (data?.properties as any)[name] ?? name));
    }

    getContent<T>(): Observable<T | undefined> {
        return this.getExercise()
            .pipe(map(data => <T>data?.content))
    }

    getInput<T>(): Observable<T | undefined> {
        return this.getExercise()
            .pipe(map(data => data?.input as T));
    }

    getInputFeedback<T>(identifier: string): Observable<T | undefined> {
        return of(undefined);
        //return this.service.getExerciseFeedback().pipe(map(data => data?.type !== "general" ? data as T : undefined));
    }

    // Actions
    async checkAnswerAsync() {
        const currentInput = await lastValueFrom(this.getInput());
        await this.submitAnswerAsync(currentInput as ExerciseAnswer);
    }

    async submitAnswerAsync(answer: ExerciseAnswer) {
        // Save the answer locally
        this.exerciseAnswer$.next(answer);

        // Save the answer to server
        const currentExercise = this.exercise$.getValue();

        if (!currentExercise)
            throw new Error("No exercise loaded");

        const feedback = await this.service.submitExerciseAnswer(currentExercise.id, answer).toPromise();
        this.exerciseFeedback$.next(feedback);
    }
}
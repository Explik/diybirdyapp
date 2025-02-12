import { Injectable } from "@angular/core";
import { Observable, BehaviorSubject, of, map, switchMap, lastValueFrom } from 'rxjs';
import { ExerciseAnswer, ExerciseStates } from "../models/exercise.interface";
import { ExerciseSessionDataService } from "./exerciseSessionData.service";
import { ExerciseDto, ExerciseFeedbackDto } from "../../../shared/api-client";

@Injectable({
    providedIn: 'root'
})
export class ExerciseService {
    private defaultInput: unknown | undefined = undefined;

    private exercise$: BehaviorSubject<ExerciseDto | undefined> = new BehaviorSubject<ExerciseDto | undefined>(undefined);
    private exersiceInput$: BehaviorSubject<unknown | undefined> = new BehaviorSubject<unknown | undefined>(undefined);
    private exerciseAnswer$: BehaviorSubject<ExerciseAnswer | undefined> = new BehaviorSubject<ExerciseAnswer | undefined>(undefined);
    private exerciseFeedback$: BehaviorSubject<ExerciseFeedbackDto | undefined> = new BehaviorSubject<ExerciseFeedbackDto | undefined>(undefined);

    constructor(private service: ExerciseSessionDataService) { }

    // State functions 
    getExercise(): Observable<ExerciseDto | undefined> {
        return this.exercise$.asObservable();
    }

    setExercise(exercise: ExerciseDto) {
        this.exercise$.next(exercise);
        this.exersiceInput$.next(exercise.input || this.clone(this.defaultInput));
        this.exerciseAnswer$.next(undefined);
        this.exerciseFeedback$.next(undefined);
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

    setDefaultInput<T>(input: T) {
        const currentInput = this.exersiceInput$.getValue();
        if (!currentInput)
            this.exersiceInput$.next(this.clone(input));

        this.defaultInput = this.clone(input);
    }

    getInput<T>(): Observable<T | undefined> {
        return this.exersiceInput$.asObservable().pipe(map(data => data as T));
    }

    getFeedbackMessage(): Observable<string | undefined> {
        return this.exerciseFeedback$.pipe(map(data => data?.message));
    }

    // Actions
    async checkAnswerAsync() {
        const currentInput = this.exersiceInput$.getValue();
        await this.submitAnswerAsync(currentInput as ExerciseAnswer);
    }

    async submitAnswerAsync(answer: ExerciseAnswer) {
        // Save the answer locally
        this.exerciseAnswer$.next(answer);

        // Save the answer to server
        const currentExercise = this.exercise$.getValue();

        if (!currentExercise)
            throw new Error("No exercise loaded");

        const exerciseWithFeedback = await this.service.submitExerciseAnswer(currentExercise.id, answer).toPromise();
        
        // Update input feedback
        if ((exerciseWithFeedback?.input as any)?.feedback) {
            const currentInput = this.exersiceInput$.getValue() as any;
            this.exersiceInput$.next({ ...currentInput, feedback: (exerciseWithFeedback!.input as any).feedback });
        }
        
        // Update general feedback
        this.exerciseFeedback$.next(exerciseWithFeedback?.feedback);
    }

    private clone(obj: any): any {
        if (!obj)
            return obj; 

        return JSON.parse(JSON.stringify(obj));
    }
}
import { Injectable } from "@angular/core";
import { Observable, BehaviorSubject, of, map, switchMap, lastValueFrom, take, finalize } from 'rxjs';
import { Exercise, ExerciseAnswer, ExerciseStates } from "../models/exercise.interface";
import { ExerciseSessionDataService } from "./exerciseSessionData.service";
import { ExerciseDto, ExerciseFeedbackDto, ExerciseSessionDto, ExerciseSessionOptionsDto, ExerciseSessionProgressDto } from "../../../shared/api-client";

export enum ExerciseSessionState {
    Idle = 'idle',
    LoadingSession = 'loading-session',
    ApplyingOptions = 'applying-options',
    RestartingSession = 'restarting-session',
    SubmittingAnswer = 'submitting-answer',
    TransitioningExercise = 'transitioning-exercise'
}

@Injectable({
    providedIn: 'root'
})
export class ExerciseService {
    private defaultInput: unknown | undefined = undefined;

    private session$: BehaviorSubject<ExerciseSessionDto|undefined> = new BehaviorSubject<ExerciseSessionDto|undefined>(undefined); 
    private exercise$: BehaviorSubject<ExerciseDto | undefined> = new BehaviorSubject<ExerciseDto | undefined>(undefined);
    private exersiceInput$: BehaviorSubject<unknown | undefined> = new BehaviorSubject<unknown | undefined>(undefined);
    private exerciseAnswer$: BehaviorSubject<ExerciseAnswer | undefined> = new BehaviorSubject<ExerciseAnswer | undefined>(undefined);
    private exerciseFeedback$: BehaviorSubject<ExerciseFeedbackDto | undefined> = new BehaviorSubject<ExerciseFeedbackDto | undefined>(undefined);
    private sessionState$: BehaviorSubject<ExerciseSessionState> = new BehaviorSubject<ExerciseSessionState>(ExerciseSessionState.Idle);

    constructor(private service: ExerciseSessionDataService) { }

    // State functions 
    loadExerciseSession(id: string) {
        this.sessionState$.next(ExerciseSessionState.LoadingSession);

        // Clear stale state immediately so loading component shows instead of old exercise
        this.exercise$.next(undefined);
        this.session$.next(undefined);
        
        this.service.getExerciseSession(id).pipe(
            finalize(() => this.sessionState$.next(ExerciseSessionState.Idle))
        ).subscribe(data => {
            this.setExerciseSession(data);
        });
    }

    getExerciseSession(): Observable<ExerciseSessionDto|undefined> {
        return this.session$.asObservable();
    }

    setExerciseSession(session?: ExerciseSessionDto) {
        this.session$.next(session);

        if (session && session.exercise) {
            this.setExercise(session.exercise);
        }
    }

    getExerciseSessionOptions(): Observable<ExerciseSessionOptionsDto|undefined> {
        return this.session$.pipe(take(1)).pipe(
            switchMap(session => {
                if (!session)
                    return of(undefined);
                
                return this.service.getOptions(session.id!);
            })
        );
    }

    applyExerciseSessionOptions(options: ExerciseSessionOptionsDto): Observable<void> {
        this.sessionState$.next(ExerciseSessionState.ApplyingOptions);

        // Clear current exercise immediately so the loading state shows while the new exercise is fetched
        this.exercise$.next(undefined);
        
        return this.session$.pipe(take(1)).pipe(
            switchMap(session => {
            if (!session)
                throw new Error("No session found");
            return this.service.applyOptions(session.id!, options);
            }),
            map(newSession => {
                this.setExerciseSession(newSession);
            }),
            finalize(() => this.sessionState$.next(ExerciseSessionState.Idle))
        );
    }

    restartExerciseSession(): Observable<ExerciseSessionDto> {
        this.sessionState$.next(ExerciseSessionState.RestartingSession);
        this.exercise$.next(undefined);

        return this.session$.pipe(take(1)).pipe(
            switchMap(session => {
                if (!session)
                    throw new Error("No session found");
                return this.service.restartSession(session.id!);
            }),
            map(newSession => {
                this.setExerciseSession(newSession);
                return newSession;
            }),
            finalize(() => this.sessionState$.next(ExerciseSessionState.Idle))
        );
    }


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
    getProgress(): Observable<ExerciseSessionProgressDto | undefined> {
        return this.session$.pipe(map(session => session?.progress));
    }
    
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

    getSessionState(): Observable<ExerciseSessionState> {
        return this.sessionState$.asObservable();
    }

    getIsBusy(): Observable<boolean> {
        return this.getSessionState().pipe(map(state => state !== ExerciseSessionState.Idle));
    }

    getIsTransitioning(): Observable<boolean> {
        return this.getSessionState().pipe(
            map(state => state === ExerciseSessionState.TransitioningExercise)
        );
    }

    // Actions
    async checkAnswerAsync() {
        const currentInput = this.exersiceInput$.getValue();
        await this.submitAnswerAsync(currentInput as ExerciseAnswer);
    }

    async nextExerciseAsync() {
        if (this.sessionState$.getValue() === ExerciseSessionState.TransitioningExercise)
            return;

        const session = this.session$.getValue();
        if (!session) 
            throw new Error("No session found");

        const previousExercise = this.exercise$.getValue();
        this.sessionState$.next(ExerciseSessionState.TransitioningExercise);
        this.exercise$.next(undefined);

        try {
            const newSession = await lastValueFrom(this.service.nextExercise(session.id!));
            this.setExerciseSession(newSession);

            if (newSession?.exercise)
                this.setExercise(newSession.exercise);
        } catch (error) {
            if (previousExercise)
                this.setExercise(previousExercise);

            throw error;
        } finally {
            this.sessionState$.next(ExerciseSessionState.Idle);
        }
    }

    async skipExerciseAsync() {
        if (this.sessionState$.getValue() === ExerciseSessionState.TransitioningExercise)
            return;

        const session = this.session$.getValue();
        if (!session) 
            throw new Error("No session found");

        const previousExercise = this.exercise$.getValue();
        this.sessionState$.next(ExerciseSessionState.TransitioningExercise);
        this.exercise$.next(undefined);

        try {
            const newSession = await lastValueFrom(this.service.skipExercise(session.id!));
            this.setExerciseSession(newSession);

            if (newSession?.exercise)
                this.setExercise(newSession.exercise);
        } catch (error) {
            if (previousExercise)
                this.setExercise(previousExercise);

            throw error;
        } finally {
            this.sessionState$.next(ExerciseSessionState.Idle);
        }
    }

    async submitAnswerAsync(answer: ExerciseAnswer) {
        this.sessionState$.next(ExerciseSessionState.SubmittingAnswer);

        try {
            // Save the answer locally
            this.exerciseAnswer$.next(answer);

            // Save the answer to server
            const currentExercise = this.exercise$.getValue();
            if (!currentExercise)
                throw new Error("No exercise loaded");

            const currentSession = this.session$.getValue();
            if (!currentSession)
                throw new Error("No session found");

            const exerciseWithFeedback = await lastValueFrom(this.service.submitExerciseAnswer(currentExercise.id, {...answer, sessionId: currentSession.id }));
            
            // Update input feedback
            if ((exerciseWithFeedback?.input as any)?.feedback) {
                const currentInput = this.exersiceInput$.getValue() as any;
                this.exersiceInput$.next({ ...currentInput, feedback: (exerciseWithFeedback!.input as any).feedback });
            }
            
            // Update general feedback
            this.exerciseFeedback$.next(exerciseWithFeedback?.feedback);
            
            // Update answer with answer ID
            const answerId = (exerciseWithFeedback?.feedback as any)?.answerId;
            if (answerId) {
                this.exerciseAnswer$.next({ ...answer, answerId });
            }
        } finally {
            this.sessionState$.next(ExerciseSessionState.Idle);
        }
    }

    async submitAnswerFeedbackAndContinue(feedbackType: string) {
        const currentAnswer = this.exerciseAnswer$.getValue();
        if (!currentAnswer?.answerId)
            throw new Error("No answer ID found");

        await this.service.submitExerciseAnswerFeedback(currentAnswer.answerId, feedbackType).toPromise();
        await this.nextExerciseAsync();
    }

    private clone(obj: any): any {
        if (!obj)
            return obj; 

        return JSON.parse(JSON.stringify(obj));
    }
}
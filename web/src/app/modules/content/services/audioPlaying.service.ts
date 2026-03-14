import { Injectable } from "@angular/core";
import { EditFlashcardAudio } from "../models/editFlashcard.model";
import { FlashcardService } from "./flashcard.service";
import { BehaviorSubject, combineLatest, distinctUntilChanged, firstValueFrom, map, Observable } from "rxjs";

export type AudioPlaybackState = 'playing' | 'paused' | 'stopped';
type PlaybackOwner = string | symbol;

@Injectable({
    providedIn: 'root'
})
export class AudioPlayingService {
    private currentAudioElement: HTMLAudioElement | null = null;
    private state$: BehaviorSubject<AudioPlaybackState> = new BehaviorSubject<AudioPlaybackState>('stopped');
    private activeOwner$: BehaviorSubject<PlaybackOwner | null> = new BehaviorSubject<PlaybackOwner | null>(null);
    private currentPlaybackResolve: (() => void) | null = null;
    private currentPlaybackReject: ((reason?: unknown) => void) | null = null;
    private currentCleanup: (() => void) | null = null;
    private playbackSessionId = 0;
    
    constructor(private dataService: FlashcardService) { }

    async startPlayingTextPronounciation(textContentId: string, owner?: PlaybackOwner): Promise<void> {
        const sessionId = this.beginPlaybackSession();
        this.stopCurrentPlayback(true);

        const blob = await firstValueFrom(this.dataService.fetchTextPronounciation(textContentId));
        if (!this.isCurrentSession(sessionId)) {
            return;
        }

        const url = URL.createObjectURL(blob);
        const audioElement = new Audio(url);
        return this.startPlayback(audioElement, owner, sessionId, () => {
            URL.revokeObjectURL(url);
        });
    }
    
    startPlayingEditFlashcard(content: EditFlashcardAudio, owner?: PlaybackOwner): Promise<void> {
        const sessionId = this.beginPlaybackSession();
        this.stopCurrentPlayback(true);

        const audioElement = content.generateElement();
        return this.startPlayback(audioElement, owner, sessionId);
    }

    pausePlaying(): void {
        if (this.currentAudioElement && !this.currentAudioElement.paused) {
            this.currentAudioElement.pause();
            this.state$.next('paused');
        }
    }

    stopPlaying(): void {
        this.beginPlaybackSession();
        this.stopCurrentPlayback(true);
    }

    getCurrentState(owner?: PlaybackOwner): Observable<AudioPlaybackState> {
        if (owner === undefined) {
            return this.state$.asObservable();
        }

        return combineLatest([this.state$, this.activeOwner$]).pipe(
            map(([state, activeOwner]) => activeOwner === owner ? state : 'stopped'),
            distinctUntilChanged()
        );
    }

    private beginPlaybackSession(): number {
        this.playbackSessionId += 1;
        return this.playbackSessionId;
    }

    private isCurrentSession(sessionId: number): boolean {
        return this.playbackSessionId === sessionId;
    }

    private startPlayback(
        audioElement: HTMLAudioElement,
        owner: PlaybackOwner | undefined,
        sessionId: number,
        cleanup?: () => void
    ): Promise<void> {
        if (!this.isCurrentSession(sessionId)) {
            cleanup?.();
            return Promise.resolve();
        }

        this.currentAudioElement = audioElement;
        this.currentCleanup = cleanup ?? null;
        this.activeOwner$.next(owner ?? null);

        return new Promise((resolve, reject) => {
            this.currentPlaybackResolve = resolve;
            this.currentPlaybackReject = reject;

            audioElement.onended = () => this.completePlayback(sessionId);
            audioElement.onerror = (error) => this.failPlayback(sessionId, error);

            const playPromise = audioElement.play();
            this.state$.next('playing');

            if (playPromise && typeof (playPromise as Promise<void>).catch === 'function') {
                void (playPromise as Promise<void>).catch((error) => {
                    this.failPlayback(sessionId, error);
                });
            }
        });
    }

    private completePlayback(sessionId: number): void {
        if (!this.isCurrentSession(sessionId)) {
            return;
        }

        this.clearCurrentPlaybackState(false);
        this.resolvePendingPlayback();
    }

    private failPlayback(sessionId: number, error: unknown): void {
        if (!this.isCurrentSession(sessionId)) {
            return;
        }

        this.clearCurrentPlaybackState(false);
        this.rejectPendingPlayback(error);
    }

    private stopCurrentPlayback(resolvePendingPromise: boolean): void {
        const hasCurrentPlayback = this.currentAudioElement !== null;
        if (!hasCurrentPlayback) {
            if (resolvePendingPromise) {
                this.resolvePendingPlayback();
            } else {
                this.currentPlaybackResolve = null;
                this.currentPlaybackReject = null;
            }
            return;
        }

        this.clearCurrentPlaybackState(true);
        if (resolvePendingPromise) {
            this.resolvePendingPlayback();
        } else {
            this.currentPlaybackResolve = null;
            this.currentPlaybackReject = null;
        }
    }

    private clearCurrentPlaybackState(resetPlaybackPosition: boolean): void {
        if (this.currentAudioElement) {
            this.currentAudioElement.onended = null;
            this.currentAudioElement.onerror = null;

            if (resetPlaybackPosition) {
                this.currentAudioElement.pause();
                this.currentAudioElement.currentTime = 0;
            }
        }

        this.currentCleanup?.();
        this.currentCleanup = null;
        this.currentAudioElement = null;

        this.activeOwner$.next(null);
        this.state$.next('stopped');
    }

    private resolvePendingPlayback(): void {
        const resolve = this.currentPlaybackResolve;
        this.currentPlaybackResolve = null;
        this.currentPlaybackReject = null;
        resolve?.();
    }

    private rejectPendingPlayback(error: unknown): void {
        const reject = this.currentPlaybackReject;
        this.currentPlaybackResolve = null;
        this.currentPlaybackReject = null;
        reject?.(error);
    }
}

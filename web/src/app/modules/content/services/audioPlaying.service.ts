import { Injectable } from "@angular/core";
import { EditFlashcardAudio } from "../models/editFlashcard.model";
import { FlashcardService } from "./flashcard.service";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class AudioPlayingService {
    private currentAudioElement: HTMLAudioElement | null = null;
    private state$: BehaviorSubject<'playing' | 'paused' | 'stopped'> = new BehaviorSubject<'playing' | 'paused' | 'stopped'>('stopped');
    
    constructor(private dataService: FlashcardService) { }

    startPlayingReviewFlashcard(flashcardId: string, flashcardSide: "left"|"right"): Promise<void> { 
        return new Promise((resolve, reject) => {
            if (this.currentAudioElement) {
                this.stopPlaying();
                this.state$.next('stopped');
                return;
            }

            this.dataService.pronounceFlashcardContent(flashcardId, flashcardSide).subscribe(blob => {
                const url = URL.createObjectURL(blob);
                this.currentAudioElement = new Audio(url);
                this.currentAudioElement.onended = () => {
                    URL.revokeObjectURL(url);
                    this.currentAudioElement = null;
                    this.state$.next('stopped');
                    resolve();
                };
                this.currentAudioElement.onerror = (error) => {
                    URL.revokeObjectURL(url);
                    this.currentAudioElement = null;
                    this.state$.next('stopped');
                    reject(error);
                };
                this.currentAudioElement.play();
                this.state$.next('playing');
            }); 
        });
    }
    
    startPlayingEditFlashcard(content: EditFlashcardAudio): Promise<void> {
        return new Promise((resolve, reject) => {
            if (this.currentAudioElement) {
                this.stopPlaying();
                this.state$.next('stopped');
                return;
            }
            this.currentAudioElement = content.generateElement();
            this.currentAudioElement.onended = () => {
                this.currentAudioElement = null;
                this.state$.next('stopped');
                resolve();
            };
            this.currentAudioElement.onerror = (error) => {
                this.currentAudioElement = null;
                this.state$.next('stopped');
                reject(error);
            };
            this.currentAudioElement.play();
            this.state$.next('playing');
        });
    }

    pausePlaying(): void {
        if (this.currentAudioElement && !this.currentAudioElement.paused) {
            this.currentAudioElement.pause();
            this.state$.next('paused');
        }
    }

    stopPlaying(): void {
        if (this.currentAudioElement) {
            this.currentAudioElement.pause();
            this.currentAudioElement.currentTime = 0;
            this.currentAudioElement = null;

            this.state$.next('stopped');
        }
    }

    getCurrentState(): Observable<'playing' | 'paused' | 'stopped'> {
        return this.state$.asObservable();
    }
}
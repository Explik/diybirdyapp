import { Injectable } from "@angular/core";
import { EditFlashcardAudio } from "../models/editFlashcard.model";
import { FlashcardService } from "./flashcard.service";

@Injectable({
    providedIn: 'root'
})
export class AudioPlayingService {
    private currentAudioElement: HTMLAudioElement | null = null;

    constructor(private dataService: FlashcardService) { }

    startPlayingReviewFlashcard(flashcardId: string, flashcardSide: "left"|"right"): Promise<void> { 
        return new Promise((resolve, reject) => {
            if (this.currentAudioElement) {
                this.stopPlaying();
            }

            this.dataService.pronounceFlashcardContent(flashcardId, flashcardSide).subscribe(blob => {
                const url = URL.createObjectURL(blob);
                this.currentAudioElement = new Audio(url);
                this.currentAudioElement.onended = () => {
                    URL.revokeObjectURL(url);
                    this.currentAudioElement = null;
                    resolve();
                };
                this.currentAudioElement.onerror = (error) => {
                    URL.revokeObjectURL(url);
                    this.currentAudioElement = null;
                    reject(error);
                };
                this.currentAudioElement.play();
            }); 
        });
    }
    
    startPlayingEditFlashcard(content: EditFlashcardAudio): Promise<void> {
        return new Promise((resolve, reject) => {
            if (this.currentAudioElement) {
                this.stopPlaying();
            }
            this.currentAudioElement = content.generateElement();
            this.currentAudioElement.onended = () => {
                this.currentAudioElement = null;
                resolve();
            };
            this.currentAudioElement.onerror = (error) => {
                this.currentAudioElement = null;
                reject(error);
            };
            this.currentAudioElement.play();
        });
    }

    stopPlaying(): void {
        if (this.currentAudioElement) {
            this.currentAudioElement.pause();
            this.currentAudioElement.currentTime = 0;
            this.currentAudioElement = null;
        }
    }
}
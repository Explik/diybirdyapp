import { Injectable } from "@angular/core";
import { AudioContent } from "../models/editAudioContent.model";

@Injectable({
    providedIn: 'root'
})
export class AudioPlayingService {
    private currentAudioElement: HTMLAudioElement | null = null;

    startPlaying(content: AudioContent): Promise<void> {
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
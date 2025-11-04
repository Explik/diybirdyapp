import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class AudioPlayingService {
    private currentAudioElement: HTMLAudioElement | null = null;

    startPlaying(content: { url: string }): Promise<void> {
        return new Promise((resolve, reject) => {
            if (this.currentAudioElement) {
                this.stopPlaying();
            }

            this.currentAudioElement = new Audio(content.url);
            this.currentAudioElement!.onended = () => {
                this.currentAudioElement = null;
                resolve();
            };
            this.currentAudioElement!.onerror = (error) => {
                this.currentAudioElement = null;
                reject(error);
            };
            this.currentAudioElement!.play();
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
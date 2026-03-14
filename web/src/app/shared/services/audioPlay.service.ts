import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class AudioPlayService {
    private baseUrl = environment.apiUrl;

    private audio?: HTMLAudioElement;
    private audioUrl?: string;

    play(audioUrl: string) {
        this.stop();

        const audio = new Audio(this.baseUrl + "/" + audioUrl);
        this.audioUrl = audioUrl;
        this.audio = audio;

        audio.addEventListener('ended', () => {
            if (this.audio !== audio)
                return;

            this.audio = undefined;
            this.audioUrl = undefined;
        });

        audio.addEventListener('error', () => {
            if (this.audio !== audio)
                return;

            console.error('Audio error', audio.error);
        });

        void audio.play().catch(error => {
            console.error('Audio play failed', error);
        });
    }

    pause(audioUrl?: string) {
        if (!this.audio)
            return;

        if (audioUrl && this.audioUrl !== audioUrl)
            return;

        this.audio.pause();
    }

    stop(audioUrl?: string) {
        if (!this.audio)
            return;

        if (audioUrl && this.audioUrl !== audioUrl)
            return;

        this.audio.pause();
        this.audio.currentTime = 0;
        this.audio = undefined;
        this.audioUrl = undefined;
    }
    
    toggle(audioUrl: string) {
        if (this.audioUrl !== audioUrl) {
            this.play(audioUrl);
        }
        else if (!this.audio || this.audio.ended) {
            this.play(audioUrl);
        } else if (this.audio.paused) {
            void this.audio.play().catch(error => {
                console.error('Audio play failed', error);
            });
        } else {
            this.audio.pause();
        }
    }
}
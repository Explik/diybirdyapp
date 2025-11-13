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
        this.audioUrl = audioUrl;
        this.audio = new Audio(this.baseUrl + "/" + audioUrl);

        this.audio.addEventListener('error', (e) => {
            if (!this.audio) return; 
            console.error('Audio error', this.audio.error);
        });

        this.audio.play();
    }

    pause() {
        if (this.audio)
            this.audio.pause();
    }
    
    toggle(audioUrl: string) {
        if (this.audioUrl !== audioUrl) {
            this.play(audioUrl);
        }
        else if (!this.audio || this.audio.ended) {
            this.play(audioUrl);
        } else if (this.audio.paused) {
            this.audio.play();
        } else {
            this.audio.pause();
    }
}
}
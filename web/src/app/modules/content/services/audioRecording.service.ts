import { Injectable } from '@angular/core';
import { BlobAudioContent } from '../models/editFlashcard.model';

@Injectable({
  providedIn: 'root'
})
export class AudioRecordingService {
  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];

  startRecording(): Promise<BlobAudioContent> {
    return new Promise((resolve, reject) => {
      navigator.mediaDevices.getUserMedia({ audio: true })
        .then(stream => {
          this.mediaRecorder = new MediaRecorder(stream);
          this.audioChunks = [];

          this.mediaRecorder.ondataavailable = event => {
            this.audioChunks.push(event.data);
          };

          this.mediaRecorder.onstop = () => {
            const audioBlob = new Blob(this.audioChunks, { type: 'audio/webm' });
            const audioBlobContent = new BlobAudioContent('recording.webm', audioBlob);

            // Release recording resources to "free" the microphone
            stream.getTracks().forEach(track => track.stop());

            resolve(audioBlobContent);
          };

          this.mediaRecorder.start();
        })
        .catch(reject);
    });
  }

  cancelRecording() {
    if (this.mediaRecorder) {
      this.mediaRecorder.stop();
      this.mediaRecorder = null;
      this.audioChunks = [];
    } 
  }

  stopRecording() {
    if (this.mediaRecorder) {
      this.mediaRecorder.stop();
    }
  }
}

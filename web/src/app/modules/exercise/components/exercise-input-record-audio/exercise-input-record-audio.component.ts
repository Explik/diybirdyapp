import { Component, Input } from '@angular/core';
import { AudioUploadService } from '../../../../shared/services/audioUpload.service';
import { CommonModule } from '@angular/common';
import { ExerciseInputRecordAudioDto, FileUploadResultDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-input-record-audio',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './exercise-input-record-audio.component.html'
})
export class ExerciseInputRecordAudioComponent {
  isRecording = false;
  audioBlob: Blob | null = null;
  mediaRecorder!: MediaRecorder;
  audioChunks: Blob[] = [];
  isUploading = false;

  @Input() input?: ExerciseInputRecordAudioDto;

  constructor(private audioUploadService: AudioUploadService) {}

  ngOnInit(): void {
    // Check if navigator.mediaDevices is available
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      alert('Your browser does not support audio recording.');
    }
  }

  async toggleRecording(): Promise<void> {
    if (this.isRecording) {
      this.stopRecording();
    } else {
      await this.startRecording();
    }
  }

  async startRecording(): Promise<void> {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.mediaRecorder = new MediaRecorder(stream);
      this.audioChunks = [];
      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) this.audioChunks.push(event.data);
      };
      this.mediaRecorder.onstop = () => {
        this.audioBlob = new Blob(this.audioChunks, { type: 'audio/webm' });

        if (this.input) {
          this.input.url = 'audio.webm'; 
          (this.input as any).files = [new File(this.audioChunks, 'audio.webm')]; 
        }
      };
      this.mediaRecorder.start();
      this.isRecording = true;
    } catch (err) {
      console.error('Error accessing audio input:', err);
    }
  }

  stopRecording(): void {
    if (this.mediaRecorder && this.isRecording) {
      this.mediaRecorder.stop();
      this.isRecording = false;
      this.uploadAudio();
    }
  }

  uploadAudio(): void {
    if (this.audioBlob) {
      this.isUploading = true;

      this.audioUploadService.uploadAudio(this.audioBlob).subscribe({
        next: (obj: FileUploadResultDto) => {
          alert('Audio uploaded successfully!');
          this.isUploading = false;
          this.audioBlob = null; // Clear the blob after upload

          this.input!.url = obj.url; 
        },
        error: (err: unknown) => {
          console.error('Error uploading audio:', err);
          this.isUploading = false;
        },
      });
    }
  }

  ngOnDestroy(): void {
    // Stop recording if the component is destroyed
    if (this.isRecording) {
      this.stopRecording();
    }
  }
}

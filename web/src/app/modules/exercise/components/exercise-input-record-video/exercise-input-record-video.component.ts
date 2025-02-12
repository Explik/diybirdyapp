import { CommonModule } from '@angular/common';
import { Component, Input, ViewChild } from '@angular/core';
import { ExerciseInputRecordVideoDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-input-record-video',
  imports: [CommonModule],
  templateUrl: './exercise-input-record-video.component.html'
})
export class ExerciseInputRecordVideoComponent {
  @ViewChild('videoElement') videoElement: any;
  @ViewChild('recordedVideo') recordedVideo: any;
  mediaStream: MediaStream | null = null;
  mediaRecorder: MediaRecorder | null = null;
  recordedChunks: Blob[] = [];
  isRecording = false;
  videoUrl: string | null = null;
  isLoading = false;
  hasFailure = false;

  @Input({required: true}) input!: ExerciseInputRecordVideoDto;
  
  ngOnInit() {
    this.isLoading = true;
    this.activateCamera();
    this.isLoading = false; 
  }

  get recordButtonLabel() {
    if (this.mediaStream === null)
      return this.hasFailure ? 'Unable to gain access to camera' : 'Enable camera to record';

    if (this.isRecording) 
      return 'Stop Recording';
    
    return this.videoUrl ? 'Tap to Re-record' : 'Tap to Record'; 
  }

  get recordButtonClasses() {
    return {
      'w-full': !this.videoUrl,
      'w-2/3': this.videoUrl,
    }
  }

  async activateCamera() {
    try {
      this.mediaStream = await navigator.mediaDevices.getUserMedia({ video: true });
      this.hasFailure = false; 
      
      // Allows Angular to render the video element before setting the srcObject
      setTimeout(() => {
        this.videoElement.nativeElement.srcObject = this.mediaStream;
      }, 0);
    } catch (error) {
      console.error('Error accessing the camera:', error);
      this.hasFailure = true;
    }
  }

  startRecording() {
    if (!this.mediaStream) return;
    this.recordedChunks = [];
    this.mediaRecorder = new MediaRecorder(this.mediaStream);
    this.mediaRecorder.ondataavailable = event => {
      if (event.data.size > 0) {
        this.recordedChunks.push(event.data);
      }
    };
    this.mediaRecorder.onstop = () => {
      const blob = new Blob(this.recordedChunks, { type: 'video/mp4' });
      this.videoUrl = URL.createObjectURL(blob);
    };
    this.mediaRecorder.start();
    this.isRecording = true;
  }

  stopRecording() {
    if (this.mediaRecorder) {
      this.mediaRecorder.stop();
      this.isRecording = false;
    }
  }

  deleteRecording() {
    this.videoUrl = null;
    this.recordedChunks = [];
  }
}

import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { AudioUploadService } from '../../../../shared/services/audioUpload.service';
import { CommonModule } from '@angular/common';
import { ExerciseInputRecordAudioDto, ExerciseInputRecordAudioFeedbackDto, FileUploadResultDto } from '../../../../shared/api-client';
import { IconComponent } from "../../../../shared/components/icon/icon.component";

@Component({
  selector: 'app-exercise-input-record-audio',
  standalone: true,
  imports: [CommonModule, IconComponent],
  templateUrl: './exercise-input-record-audio.component.html'
})
export class ExerciseInputRecordAudioComponent implements OnInit, OnChanges, OnDestroy {
  isRecording = false;
  audioBlob: Blob | null = null;
  mediaRecorder!: MediaRecorder;
  audioChunks: Blob[] = [];

  feedbackValues: { state: string, value: string}[] = []; 
  
  @Input({required: true}) input?: ExerciseInputRecordAudioDto;
  @Output()  recordingFinished: EventEmitter<void> = new EventEmitter<void>();

  constructor(private audioUploadService: AudioUploadService) {}

  ngOnInit(): void {
    // Check if navigator.mediaDevices is available
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      alert('Your browser does not support audio recording.');
    }

    this.updateValues(this.input);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['input']) {
      this.updateValues(changes['input'].currentValue);
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
          this.recordingFinished?.emit();
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
    }
  }

  ngOnDestroy(): void {
    // Stop recording if the component is destroyed
    if (this.isRecording) {
      this.stopRecording();
    }
  }

  updateValues(newValue: ExerciseInputRecordAudioDto | undefined) {
      const feedbackValues: { state: string, value: string}[] = [];
  
      if (newValue?.feedback?.correctValues) 
        feedbackValues.push(...newValue.feedback.correctValues.map(value => ({ state: 'success', value })));
      
      if (newValue?.feedback?.incorrectValues) 
        feedbackValues.push(...newValue.feedback.incorrectValues.map(value => ({ state: 'failure', value })));
  
      this.feedbackValues = feedbackValues;
    }
}

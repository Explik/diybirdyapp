import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, ViewChild, ElementRef, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { EditFlashcardVideo, EditFlashcardVideoImpl } from '../../models/editFlashcard.model';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: 'app-video-input',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => VideoInputComponent),
    multi: true
  }],
  templateUrl: './video-input.component.html',
  styleUrls: ['./video-input.component.css']
})
export class VideoInputComponent implements ControlValueAccessor {
  @Input() videoData: EditFlashcardVideo | undefined;
  @Output() videoDataChange = new EventEmitter<EditFlashcardVideo | undefined>();
  @ViewChild('liveVideoElement') liveVideoElement!: ElementRef<HTMLVideoElement>;

  // ControlValueAccessor callbacks
  private onChange: (value: EditFlashcardVideo | undefined) => void = () => {};
  private onTouched: () => void = () => {};
  isDisabled = false;

  get videoFile(): EditFlashcardVideoImpl | undefined {
    if (this.videoData instanceof EditFlashcardVideoImpl && this.videoData.videoFile) {
      return this.videoData;
    }
    return undefined;
  }

  get videoFileName(): string | undefined {
    const MAX_FILENAME_LENGTH = 20;
    const name = this.videoFile?.videoFile?.name;
    if (!name) return undefined;

    const dotIndex = name.lastIndexOf('.');
    if (dotIndex === -1 || name.length <= MAX_FILENAME_LENGTH) return name;

    const ext = name.substring(dotIndex);
    const base = name.substring(0, dotIndex);
    const maxBaseLength = MAX_FILENAME_LENGTH - ext.length - 3; // 3 for "..."
    if (maxBaseLength <= 0) return '...' + ext;
    
    if (base.length > maxBaseLength) {
      return base.substring(0, maxBaseLength) + '...' + ext;
    }
    return name;
  }

  get visualState(): 'empty' | 'preview' | 'recording' | 'saving' | 'dropping' {
    if (this.isSaving) {
      return 'saving';
    } else if (this.isRecording) {
      return 'recording';
    } else if (this.videoData) {
      return 'preview';
    } else if (this.isDragging) {
      return 'dropping';
    }
    return 'empty';
  }

  isPlaying = false;
  isRecording = false;
  isSaving = false;
  isDragging = false;
  mediaStream: MediaStream | null = null;
  mediaRecorder: MediaRecorder | null = null;
  recordedChunks: Blob[] = [];

  handleFileInput(event: any) {
    const file = event.target.files[0];
    if (file && file.type.startsWith('video/')) {
      this.videoData = EditFlashcardVideoImpl.createFromFile(file);
      this.videoDataChange.emit(this.videoData);
      this.onChange(this.videoData);
      this.onTouched();
    }
  }

  async startRecording() {
    try {
      this.mediaStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      this.isRecording = true;
      
      // Show live video feed
      setTimeout(() => {
        if (this.liveVideoElement && this.mediaStream) {
          this.liveVideoElement.nativeElement.srcObject = this.mediaStream;
        }
      }, 0);
      
      this.recordedChunks = [];
      this.mediaRecorder = new MediaRecorder(this.mediaStream);
      
      this.mediaRecorder.ondataavailable = event => {
        if (event.data.size > 0) {
          this.recordedChunks.push(event.data);
        }
      };
      
      this.mediaRecorder.onstop = () => {
        const blob = new Blob(this.recordedChunks, { type: 'video/mp4' });
        const file = new File([blob], `recorded-video-${Date.now()}.mp4`, { type: 'video/mp4' });
        this.videoData = EditFlashcardVideoImpl.createFromFile(file);
        this.videoDataChange.emit(this.videoData);
        this.onChange(this.videoData);
        this.onTouched();
        this.isRecording = false;
        this.isSaving = false;
        
        // Clean up media stream
        if (this.mediaStream) {
          this.mediaStream.getTracks().forEach(track => track.stop());
          this.mediaStream = null;
        }
      };
      
      this.mediaRecorder.start();
    } catch (error) {
      console.error('Error accessing camera:', error);
      alert('Unable to access camera. Please check your permissions.');
      this.isRecording = false;
    }
  }

  saveRecording() {
    if (this.mediaRecorder && this.isRecording) {
      this.isRecording = false;
      this.isSaving = true;
      this.mediaRecorder.stop();
    }
  }

  cancelRecording() {
    if (this.mediaRecorder && this.isRecording) {
      this.mediaRecorder.stop();
      this.recordedChunks = [];
      this.videoData = undefined;
      this.videoDataChange.emit(undefined);
      this.onChange(undefined);
      this.onTouched();
    }
    
    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach(track => track.stop());
      this.mediaStream = null;
    }
    
    this.isRecording = false;
    this.isSaving = false;
  }

  clearFileInput() {
    this.videoData = undefined;
    this.videoDataChange.emit(undefined);
    this.onChange(undefined);
    this.onTouched();
  }

  handleDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files.length) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('video/')) {
        this.videoData = EditFlashcardVideoImpl.createFromFile(file);
        this.videoDataChange.emit(this.videoData);
        this.onChange(this.videoData);
        this.onTouched();
      }
    }
  }

  allowDrop(event: DragEvent) {
    if (this.visualState === 'empty' || this.visualState === 'dropping') {
      event.preventDefault();
    }
  }

  onDragEnter(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    // Only set isDragging to false if we're actually leaving the drop zone
    // Check if the related target is outside the drop zone
    const target = event.currentTarget as HTMLElement;
    const relatedTarget = event.relatedTarget as HTMLElement;
    
    if (!target.contains(relatedTarget)) {
      this.isDragging = false;
    }
  }

  toggleVideo() {
    if (!this.videoData) 
      return;

    // Find video element and toggle play/pause
    const videoElement = document.querySelector('video') as HTMLVideoElement;
    if (videoElement) {
      if (videoElement.paused) {
        videoElement.play();
        this.isPlaying = true;
      } else {
        videoElement.pause();
        this.isPlaying = false;
      }
    }
  }

  // ControlValueAccessor implementation
  writeValue(obj: EditFlashcardVideo | undefined): void {
    this.videoData = obj;
  }
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  setDisabledState?(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
    if (isDisabled) {
      // if disabling, ensure any ongoing recording/streams are stopped
      if (this.mediaRecorder && this.isRecording) {
        try { this.mediaRecorder.stop(); } catch {}
      }
      if (this.mediaStream) {
        this.mediaStream.getTracks().forEach(track => track.stop());
        this.mediaStream = null;
      }
      this.isRecording = false;
      this.isSaving = false;
    }
  }
}

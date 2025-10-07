import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AudioRecordingService } from '../../services/audioRecording.service';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { CommonModule } from '@angular/common';
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { EditFlashcardAudio, FileAudioContent } from '../../models/editFlashcard.model';

@Component({
  selector: 'app-audio-input',
  templateUrl: './audio-input.component.html',
  styleUrls: ['./audio-input.component.css'],
  imports: [CommonModule, FormsModule]
})
export class AudioInputComponent {
  @Input() audioData: EditFlashcardAudio | undefined;
  @Output() audioDataChange = new EventEmitter<EditFlashcardAudio | undefined>();

  get audioFile(): FileAudioContent | undefined {
    if (this.audioData instanceof FileAudioContent) {
      return this.audioData;
    }
    return undefined;
  }

  get audioFileName(): string | undefined {
    const MAX_FILENAME_LENGTH = 20;
    const name = this.audioFile?.name;
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

  get visualState(): 'empty' | 'preview' | 'recording' | 'dropping'  {
    if (this.isRecording) {
      return 'recording';
    } else if (this.audioData) {
      return 'preview';
    } else if (this.isDragging) {
      return 'dropping';
    }
    return 'empty';
  }

  isRecording = false;
  isPlaying = false;
  isDropping = false;
  isDragging = false;

  constructor(
    private playService: AudioPlayingService,
    private recordingService: AudioRecordingService) {
      this.playService.getCurrentState().subscribe(state => {
        this.isPlaying = (state === 'playing');
      });
    }

  startRecording() {
    this.isRecording = true;
    this.recordingService.startRecording().then(content => {
      this.isRecording = false;

      this.audioData = content;
      this.audioDataChange.emit(content);
    });
  }

  saveRecording() {
    this.recordingService.stopRecording();
  }

  cancelRecording() {
    this.isRecording = false;
    this.recordingService.cancelRecording();
  }

  handleFileInput(event: any) {
    const file = event.target.files[0];
    if (file && file.type.startsWith('audio/')) {
      this.audioData = new FileAudioContent(file);
      this.audioDataChange.emit(this.audioData);
    }
  }

  clearFileInput() {
    this.audioData = undefined;
    this.audioDataChange.emit(undefined);
  }

  handleDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files.length) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('audio/')) {
        this.audioData = new FileAudioContent(file);
        this.audioDataChange.emit(this.audioData);
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

  createAudioUrl(file: File) {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.audioData = new FileAudioContent(e.target.result);
    };
    reader.readAsDataURL(file);
  }

  toogleAudio() {
    if (!this.audioData) 
      return;

    this.playService.startPlayingEditFlashcard(this.audioData);
  }
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AudioRecordingService } from '../../services/audioRecording.service';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { CommonModule } from '@angular/common';
import { AudioContent, FileAudioContent } from '../../models/editAudioContent.model';
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-audio-input',
  templateUrl: './audio-input.component.html',
  styleUrls: ['./audio-input.component.css'],
  imports: [CommonModule, FormsModule]
})
export class AudioInputComponent {
  @Input() audioData: AudioContent | undefined;
  @Output() audioDataChange = new EventEmitter<AudioContent | undefined>();

  get audioFile(): FileAudioContent | undefined {
    if (this.audioData instanceof FileAudioContent) {
      return this.audioData;
    }
    return undefined;
  }

  isRecording = false;

  constructor(
    private playService: AudioPlayingService,
    private recordingService: AudioRecordingService) {}

  startRecording() {
    this.isRecording = true;
    this.recordingService.startRecording().then(content => {
      this.isRecording = false;

      this.audioData = content;
      this.audioDataChange.emit(content);
    });
  }

  stopRecording() {
    this.recordingService.stopRecording();
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
    if (event.dataTransfer?.files.length) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('audio/')) {
        this.audioData = new FileAudioContent(file);
        this.audioDataChange.emit(this.audioData);
      }
    }
  }

  allowDrop(event: DragEvent) {
    event.preventDefault();
  }

  createAudioUrl(file: File) {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.audioData = new FileAudioContent(e.target.result);
    };
    reader.readAsDataURL(file);
  }

  playAudio() {
    if (this.audioData) 
      this.playService.startPlaying(this.audioData);
  }
}

import { Component, EventEmitter, forwardRef, Input, Output } from '@angular/core';
import { AudioRecordingService } from '../../services/audioRecording.service';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { CommonModule } from '@angular/common';
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { FormsModule, NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';
import { EditFlashcardAudio, FileAudioContent } from '../../models/editFlashcard.model';

@Component({
  selector: 'app-audio-input',
  templateUrl: './audio-input.component.html',
  styleUrls: ['./audio-input.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AudioInputComponent),
      multi: true
    }
  ]
})
export class AudioInputComponent implements ControlValueAccessor {
  // Keep the EventEmitter for template / parent-component compatibility.
  @Output() audioDataChange = new EventEmitter<EditFlashcardAudio | undefined>();

  // Internal backing field for the Input / form value
  private _audioData: EditFlashcardAudio | undefined;

  // Allow template consumers to still bind [audioData]. When used as a form control,
  // writeValue will update this backing field; when the user changes the value,
  // onChange will be called and the emitter will fire.
  @Input()
  set audioData(value: EditFlashcardAudio | undefined) {
    this._audioData = value;
  }
  get audioData(): EditFlashcardAudio | undefined {
    return this._audioData;
  }

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
  private isDisabled = false;

  // ControlValueAccessor callbacks
  private onChange: (value: any) => void = (_: any) => {};
  private onTouched: () => void = () => {};

  constructor(
    private playService: AudioPlayingService,
    private recordingService: AudioRecordingService) {
      this.playService.getCurrentState().subscribe(state => {
        this.isPlaying = (state === 'playing');
      });
    }

  startRecording() {
    if (this.isDisabled) return;
    // mark touched for forms
    try { this.onTouched(); } catch {}

    this.isRecording = true;
    this.recordingService.startRecording().then(content => {
      this.isRecording = false;

      this.setValue(content);
    });
  }

  saveRecording() {
    if (this.isDisabled) return;
    this.recordingService.stopRecording();
  }

  cancelRecording() {
    if (this.isDisabled) return;
    try { this.onTouched(); } catch {}
    this.isRecording = false;
    this.recordingService.cancelRecording();
  }

  handleFileInput(event: any) {
    if (this.isDisabled) return;
    try { this.onTouched(); } catch {}
    const file = event.target.files[0];
    if (file && file.type.startsWith('audio/')) {
      this.setValue(new FileAudioContent(file));
    }
  }

  clearFileInput() {
    if (this.isDisabled) return;
    try { this.onTouched(); } catch {}
    this.setValue(undefined);
  }

  handleDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (this.isDisabled) return;
    try { this.onTouched(); } catch {}
    if (event.dataTransfer?.files.length) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('audio/')) {
        this.setValue(new FileAudioContent(file));
      }
    }
  }

  allowDrop(event: DragEvent) {
    if (this.isDisabled) return;
    if (this.visualState === 'empty' || this.visualState === 'dropping') {
      event.preventDefault();
    }
  }

  onDragEnter(event: DragEvent) {
    event.preventDefault();
    if (this.isDisabled) return;
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
      this.setValue(new FileAudioContent(e.target.result));
    };
    reader.readAsDataURL(file);
  }

  toogleAudio() {
    if (this.isDisabled) return;
    try { this.onTouched(); } catch {}
    if (!this.audioData)
      return;

    this.playService.startPlayingEditFlashcard(this.audioData);
  }

  // Helper to set the internal value and notify Angular forms / parent bindings
  private setValue(value: EditFlashcardAudio | undefined, fromWriteValue = false) {
    this._audioData = value;

    // Always update the view binding emitter for local template parents when user-driven.
    if (!fromWriteValue) {
      try {
        this.onChange(value);
      } catch {}
      this.audioDataChange.emit(value);
    }
  }

  // ControlValueAccessor implementation
  writeValue(obj: EditFlashcardAudio | undefined): void {
    // When the form writes a value, update internal state but don't call onChange again.
    this.setValue(obj, true);
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }
}

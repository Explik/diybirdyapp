import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, ViewChild, ElementRef, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { EditFlashcardImage, EditFlashcardImageImpl } from '../../models/editFlashcard.model';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: 'app-image-input',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => ImageInputComponent),
    multi: true
  }],
  templateUrl: './image-input.component.html',
  styleUrls: ['./image-input.component.css']
})
export class ImageInputComponent implements ControlValueAccessor {
  @Input() imageData: EditFlashcardImage | undefined;
  @Output() imageDataChange = new EventEmitter<EditFlashcardImage | undefined>();
  @ViewChild('liveVideoElement') liveVideoElement!: ElementRef<HTMLVideoElement>;
  @ViewChild('canvas') canvasElement!: ElementRef<HTMLCanvasElement>;

  // ControlValueAccessor callbacks
  private onChange: (value: EditFlashcardImage | undefined) => void = () => {};
  private onTouched: () => void = () => {};
  isDisabled = false;

  get imageFile(): EditFlashcardImageImpl | undefined {
    if (this.imageData instanceof EditFlashcardImageImpl && this.imageData.imageFile) {
      return this.imageData;
    }
    return undefined;
  }

  get imageFileName(): string | undefined {
    const MAX_FILENAME_LENGTH = 20;
    const name = this.imageFile?.imageFile?.name;
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

  get visualState(): 'empty' | 'preview' | 'capturing' | 'dropping' {
    if (this.isCapturing) {
      return 'capturing';
    } else if (this.imageData) {
      return 'preview';
    } else if (this.isDragging) {
      return 'dropping';
    }
    return 'empty';
  }

  isCapturing = false;
  isDragging = false;
  mediaStream: MediaStream | null = null;

  handleFileInput(event: any) {
    const file = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      this.imageData = EditFlashcardImageImpl.createFromFile(file);
      this.imageDataChange.emit(this.imageData);
      this.onChange(this.imageData);
      this.onTouched();
    }
  }

  async startCapturing() {
    try {
      this.mediaStream = await navigator.mediaDevices.getUserMedia({ video: true });
      this.isCapturing = true;
      
      // Show live video feed
      setTimeout(() => {
        if (this.liveVideoElement && this.mediaStream) {
          this.liveVideoElement.nativeElement.srcObject = this.mediaStream;
        }
      }, 0);
    } catch (error) {
      console.error('Error accessing camera:', error);
      alert('Unable to access camera. Please check your permissions.');
      this.isCapturing = false;
    }
  }

  captureImage() {
    if (!this.mediaStream || !this.liveVideoElement || !this.canvasElement) return;

    const video = this.liveVideoElement.nativeElement;
    const canvas = this.canvasElement.nativeElement;
    const context = canvas.getContext('2d');

    if (!context) return;

    // Set canvas dimensions to match video
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    // Draw the current video frame to canvas
    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    // Convert canvas to blob and create file
    canvas.toBlob((blob) => {
      if (blob) {
        const file = new File([blob], `captured-image-${Date.now()}.png`, { type: 'image/png' });
        this.imageData = EditFlashcardImageImpl.createFromFile(file);
        this.imageDataChange.emit(this.imageData);
        this.onChange(this.imageData);
        this.onTouched();
        this.stopCapturing();
      }
    }, 'image/png');
  }

  stopCapturing() {
    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach(track => track.stop());
      this.mediaStream = null;
    }
    this.isCapturing = false;
  }

  clearFileInput() {
    this.imageData = undefined;
    this.imageDataChange.emit(undefined);
    this.onChange(undefined);
    this.onTouched();
  }

  handleDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files.length) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('image/')) {
        this.imageData = EditFlashcardImageImpl.createFromFile(file);
        this.imageDataChange.emit(this.imageData);
        this.onChange(this.imageData);
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

  // ControlValueAccessor implementation
  writeValue(obj: EditFlashcardImage | undefined): void {
    this.imageData = obj;
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
      // if disabling, ensure any capture/streams are stopped
      this.stopCapturing();
    }
  }
}

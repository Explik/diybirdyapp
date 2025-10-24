import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExerciseInputSelectOptionsDto } from '../../../../shared/api-client';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { RowButtonComponent } from "../../../../shared/components/row-button/row-button.component";
import { SelectOptionInputAudioOption, SelectOptionInputImageOption, SelectOptionInputOption, SelectOptionInputTextOption } from '../../../../shared/api-client/model/select-option-input-text-option';

@Component({
  selector: 'app-exercise-input-select-options',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent, RowButtonComponent],
  templateUrl: './exercise-input-select-options.component.html',
  styleUrl: './exercise-input-select-options.component.css'
})
export class ExerciseInputSelectOptionsComponent {
  @Input() input: ExerciseInputSelectOptionsDto | undefined = undefined;
  @Output()  optionSelected: EventEmitter<string> = new EventEmitter<string>();

  constructor(private audioService: AudioPlayingService) { }

  castAsAudio(option: SelectOptionInputOption): SelectOptionInputAudioOption {
    return option as SelectOptionInputAudioOption;
  }

  castAsText(option: SelectOptionInputOption): SelectOptionInputTextOption {
    return option as SelectOptionInputTextOption;
  }

  castAsImage(option: SelectOptionInputOption): SelectOptionInputImageOption {
    return option as SelectOptionInputImageOption;
  }

  castAsVideo(option: SelectOptionInputOption): SelectOptionInputAudioOption {
    return option as SelectOptionInputAudioOption;
  }

  handleOptionSelected(optionId: string): void {
    if (this.input && !this.input.feedback)
      this.optionSelected.emit(optionId);
  }

  handleAudioPlay(optionId: string): void {
    if (!this.input) return;

    const option = this.input.options!.find(o => o.id === optionId);
    if (!option) return;

    this.audioService.startPlaying(option as SelectOptionInputAudioOption); 
  }
}

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExerciseInputSelectOptionsDto, SelectOptionInputBaseOption } from '../../../../shared/api-client';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { RowButtonComponent } from "../../../../shared/components/row-button/row-button.component";
import { SelectOptionInputAudioOption, SelectOptionInputImageOption, SelectOptionInputOption, SelectOptionInputTextOption } from '../../../../shared/api-client/model/select-option-input-text-option';
import { HotkeyService } from '../../../../shared/services/hotKey.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-exercise-input-select-options',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent, RowButtonComponent],
  templateUrl: './exercise-input-select-options.component.html',
  styleUrl: './exercise-input-select-options.component.css',
  providers: [HotkeyService]
})
export class ExerciseInputSelectOptionsComponent implements OnChanges, OnDestroy {
  private subs = new Subscription();
  
  @Input() input: ExerciseInputSelectOptionsDto | undefined = undefined;
  @Output()  optionSelected: EventEmitter<string> = new EventEmitter<string>();

  constructor(private audioService: AudioPlayingService, private hotkeyService: HotkeyService) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['input'] && this.input?.options) {
      // Clear existing hotkey subscriptions
      this.subs.unsubscribe();
      this.subs = new Subscription();
      
      // Set up hotkeys dynamically based on the number of options
      this.input.options.forEach((option, index) => {
        const key = (index + 1).toString();
        this.subs.add(
          this.hotkeyService.onHotkey({ key }).subscribe(() => this.selectOptionByIndex(index))
        );
      });
    }
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  selectOptionByIndex(index: number): void {
    if (!this.input || !this.input.options || index >= this.input.options.length) return;
    
    const option = this.input.options[index];
    this.handleOptionSelected(option.id!);
  }

  castAsAudio(option: SelectOptionInputBaseOption): SelectOptionInputAudioOption {
    return option as SelectOptionInputAudioOption;
  }

  castAsText(option: SelectOptionInputBaseOption): SelectOptionInputTextOption {
    return option as SelectOptionInputTextOption;
  }

  castAsImage(option: SelectOptionInputBaseOption): SelectOptionInputImageOption {
    return option as SelectOptionInputImageOption;
  }

  castAsVideo(option: SelectOptionInputBaseOption): SelectOptionInputAudioOption {
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

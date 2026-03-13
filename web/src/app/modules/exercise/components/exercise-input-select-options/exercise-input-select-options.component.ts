
import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExerciseInputSelectOptionsDto, SelectOptionInputBaseOption } from '../../../../shared/api-client';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { AudioPlayingService } from '../../services/audioPlaying.service';
import { RowButtonComponent } from "../../../../shared/components/row-button/row-button.component";
import { SelectOptionInputAudioOption, SelectOptionInputImageOption, SelectOptionInputTextOption, SelectOptionInputVideoOption } from '../../../../shared/api-client/model/select-option-input-text-option';
import { HotkeyService } from '../../../../shared/services/hotKey.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-exercise-input-select-options',
  standalone: true,
  imports: [FormsModule, IconComponent, RowButtonComponent],
  templateUrl: './exercise-input-select-options.component.html',
  styleUrl: './exercise-input-select-options.component.css',
  providers: [HotkeyService]
})
export class ExerciseInputSelectOptionsComponent implements OnChanges, OnDestroy {
  private subs = new Subscription();
  private readonly longTextVerticalLayoutThreshold = 24;
  
  @Input() input: ExerciseInputSelectOptionsDto | undefined = undefined;
  @Output()  optionSelected: EventEmitter<string> = new EventEmitter<string>();
  
  optionsVisible: boolean = false;
  isVerticalOptionsLayout: boolean = false;

  constructor(private audioService: AudioPlayingService, private hotkeyService: HotkeyService) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['input']) {
      return;
    }

    this.isVerticalOptionsLayout = this.shouldUseVerticalOptionsLayout();

    if (!this.input?.feedback) {
      this.optionsVisible = !this.input?.initiallyHideOptions;
    }

    // Clear existing hotkey subscriptions before wiring new option shortcuts.
    this.subs.unsubscribe();
    this.subs = new Subscription();

    this.input?.options?.forEach((option, index) => {
      const key = (index + 1).toString();
      this.subs.add(
        this.hotkeyService.onHotkey({ key }).subscribe(() => this.selectOptionByIndex(index))
      );
    });
  }

  get optionHeightClass(): string {
    return this.isVerticalOptionsLayout ? 'h-28' : 'h-14';
  }

  private shouldUseVerticalOptionsLayout(): boolean {
    if (!this.input) {
      return false;
    }

    if (this.input.optionType === 'image' || this.input.optionType === 'video') {
      return true;
    }

    if (this.input.optionType !== 'text') {
      return false;
    }

    return (this.input.options ?? []).some(option => this.isLongTextOption(option));
  }

  private isLongTextOption(option: SelectOptionInputBaseOption): boolean {
    const optionText = (option as SelectOptionInputTextOption).text;
    return typeof optionText === 'string' && optionText.trim().length > this.longTextVerticalLayoutThreshold;
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

  castAsVideo(option: SelectOptionInputBaseOption): SelectOptionInputVideoOption {
    return option as SelectOptionInputVideoOption;
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
  
  handleShowOptions(): void {
    this.optionsVisible = true;
  }
}

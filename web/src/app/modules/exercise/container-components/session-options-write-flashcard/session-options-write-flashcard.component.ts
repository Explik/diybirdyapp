
import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnDestroy, LOCALE_ID, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { SelectComponent } from '../../../../shared/components/select/select.component';
import { OptionComponent } from '../../../../shared/components/option/option.component';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseSessionOptionsLanguageOptionDto, ExerciseSessionOptionsWriteFlashcardsDto } from '../../../../shared/api-client';
import { SessionOptionsComponent } from '../../models/component.interface';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-session-options-write-flashcard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SelectComponent, OptionComponent, FormFieldComponent, LabelComponent, SlideToogleComponent],
  templateUrl: './session-options-write-flashcard.component.html'
})
export class SessionOptionsWriteFlashcardComponent implements OnInit, OnChanges, OnDestroy, SessionOptionsComponent<ExerciseSessionOptionsWriteFlashcardsDto> {
  @Input() options?: ExerciseSessionOptionsWriteFlashcardsDto;
  @Output() optionsChange: EventEmitter<ExerciseSessionOptionsWriteFlashcardsDto> = new EventEmitter();

  form: FormGroup;
  availableAnswerLanguages: Array<ExerciseSessionOptionsLanguageOptionDto> = [];
  private sub: Subscription | undefined = undefined;
  private displayNames: Intl.DisplayNames;

  constructor(private fb: FormBuilder, @Inject(LOCALE_ID) private locale: string) {
    this.form = this.fb.group({
      answerLanguageId: [''],
      retypeCorrectAnswerEnabled: [false],
      textToSpeechEnabled: [false]
    });
    this.displayNames = new Intl.DisplayNames([this.locale], { type: 'language' });
  }

  ngOnInit(): void {
    this.sub = this.form.valueChanges.subscribe(_ => {
      if (!this.options) return;
      this.optionsChange.emit(this.buildDtoFromForm());
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['options']) {
      this.patchFormFromOptions();
    }
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  private patchFormFromOptions() {
    const options = this.options || ({} as ExerciseSessionOptionsWriteFlashcardsDto);
    this.availableAnswerLanguages = options.availableAnswerLanguages || [];

    this.form.patchValue({
      answerLanguageId: options.answerLanguageId || '',
      retypeCorrectAnswerEnabled: options.retypeCorrectAnswerEnabled || false,
      textToSpeechEnabled: options.textToSpeechEnabled || false
    }, { emitEvent: false });
  }

  private buildDtoFromForm(): ExerciseSessionOptionsWriteFlashcardsDto {
    return {
      type: this.options?.type,
      answerLanguageId: this.form.get('answerLanguageId')?.value || '',
      retypeCorrectAnswerEnabled: !!this.form.get('retypeCorrectAnswerEnabled')?.value,
      textToSpeechEnabled: !!this.form.get('textToSpeechEnabled')?.value
    } as ExerciseSessionOptionsWriteFlashcardsDto;
  }

  public formatLanguageName(language: ExerciseSessionOptionsLanguageOptionDto) {
    if (language.isoCode) {
      return this.displayNames.of(language.isoCode) || 'N/A';
    }
    return 'N/A';
  }
}

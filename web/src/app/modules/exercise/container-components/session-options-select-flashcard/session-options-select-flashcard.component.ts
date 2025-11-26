import { Component, EventEmitter, Input, OnInit, Output, OnChanges, SimpleChanges, OnDestroy, Inject, LOCALE_ID } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { SelectComponent } from '../../../../shared/components/select/select.component';
import { OptionComponent } from '../../../../shared/components/option/option.component';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseSessionOptionsLanguageOptionDto, ExerciseSessionOptionsSelectFlashcardsDto } from '../../../../shared/api-client';
import { SessionOptionsComponent } from '../../models/component.interface';
import { Subscription } from 'rxjs';

/**
 * Small reactive form for session options when selecting "review flashcards".
 * - initialFlashcardLanguageId: string | ''
 * - textToSpeechEnabled: boolean
 */
@Component({
  selector: 'app-session-options-select-flashcard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SelectComponent, OptionComponent, FormFieldComponent, LabelComponent, SlideToogleComponent],
  templateUrl: './session-options-select-flashcard.component.html'
})
export class SessionOptionsSelectFlashcardComponent implements OnInit, OnChanges, OnDestroy, SessionOptionsComponent<ExerciseSessionOptionsSelectFlashcardsDto> {
  @Input() options?: ExerciseSessionOptionsSelectFlashcardsDto;
  @Output() optionsChange: EventEmitter<ExerciseSessionOptionsSelectFlashcardsDto> = new EventEmitter();

  form: FormGroup;
  availableLanguages: Array<ExerciseSessionOptionsLanguageOptionDto> = [];
  private sub: Subscription | undefined = undefined;
  private displayNames: Intl.DisplayNames;

  constructor(private fb: FormBuilder, @Inject(LOCALE_ID) private locale: string) {
    this.form = this.fb.group({
      initialFlashcardLanguageId: [''],
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
    const options = this.options || ({} as ExerciseSessionOptionsSelectFlashcardsDto);
    this.availableLanguages = options.availableFlashcardLanguages || [];

    this.form.patchValue({
      initialFlashcardLanguageId: options.initialFlashcardLanguageId || '',
      textToSpeechEnabled: options.textToSpeechEnabled || false
    }, { emitEvent: false });
  }

  private buildDtoFromForm(): ExerciseSessionOptionsSelectFlashcardsDto {
    return {
      type: this.options?.type,
      initialFlashcardLanguageId: this.form.get('initialFlashcardLanguageId')?.value || '',
      textToSpeechEnabled: !!this.form.get('textToSpeechEnabled')?.value
    } as ExerciseSessionOptionsSelectFlashcardsDto;
  }

  public formatLanguageName(language: ExerciseSessionOptionsLanguageOptionDto) {
    if (language.isoCode) {
      return this.displayNames.of(language.isoCode) || 'N/A';
    }
    return 'N/A';
  }
}

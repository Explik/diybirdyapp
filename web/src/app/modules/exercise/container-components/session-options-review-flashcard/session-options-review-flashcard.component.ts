import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnDestroy, LOCALE_ID, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { SelectComponent } from '../../../../shared/components/select/select.component';
import { OptionComponent } from '../../../../shared/components/option/option.component';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseSessionOptionsLanguageOptionDto, ExerciseSessionOptionsReviewFlashcardsDto } from '../../../../shared/api-client';
import { SessionOptionsComponent } from '../../models/component.interface';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-session-options-review-flashcard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SelectComponent, OptionComponent, FormFieldComponent, LabelComponent, SlideToogleComponent],
  templateUrl: './session-options-review-flashcard.component.html'
})
export class SessionOptionsReviewFlashcardComponent implements OnInit, OnChanges, OnDestroy, SessionOptionsComponent<ExerciseSessionOptionsReviewFlashcardsDto> {
  @Input() options?: ExerciseSessionOptionsReviewFlashcardsDto;
  @Output() optionsChange: EventEmitter<ExerciseSessionOptionsReviewFlashcardsDto> = new EventEmitter();

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
    const options = this.options || ({} as ExerciseSessionOptionsReviewFlashcardsDto);
    this.availableLanguages = options.availableFlashcardLanguages || [];

    this.form.patchValue({
      initialFlashcardLanguageId: options.initialFlashcardLanguageId || '',
      textToSpeechEnabled: options.textToSpeechEnabled || false
    }, { emitEvent: false });
  }

  private buildDtoFromForm(): ExerciseSessionOptionsReviewFlashcardsDto {
    return {
      type: this.options?.type,
      initialFlashcardLanguageId: this.form.get('initialFlashcardLanguageId')?.value || '',
      textToSpeechEnabled: !!this.form.get('textToSpeechEnabled')?.value
    } as ExerciseSessionOptionsReviewFlashcardsDto;
  }

  public formatLanguageName(language: ExerciseSessionOptionsLanguageOptionDto) {
    if (language.isoCode) {
      return this.displayNames.of(language.isoCode) || 'N/A';
    }
    return 'N/A';
  }
}

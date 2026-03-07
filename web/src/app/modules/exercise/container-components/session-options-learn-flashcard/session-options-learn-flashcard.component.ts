
import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnDestroy, Inject, LOCALE_ID } from '@angular/core';

import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, AbstractControl, ValidationErrors } from '@angular/forms';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseSessionOptionsLanguageOptionDto, ExerciseSessionOptionsLearnFlashcardsDto } from '../../../../shared/api-client';
import { SessionOptionsComponent } from '../../models/component.interface';
import { Subscription } from 'rxjs';
import { FormErrorComponent } from "../../../../shared/components/form-error/form-error.component";

@Component({
  selector: 'app-session-options-learn-flashcard',
  standalone: true,
  imports: [ReactiveFormsModule, FormFieldComponent, LabelComponent, SlideToogleComponent, FormErrorComponent, FormErrorComponent],
  templateUrl: './session-options-learn-flashcard.component.html'
})
export class SessionOptionsLearnFlashcardComponent implements OnInit, OnChanges, OnDestroy, SessionOptionsComponent<ExerciseSessionOptionsLearnFlashcardsDto> {
  @Input() options?: ExerciseSessionOptionsLearnFlashcardsDto;
  @Output() optionsChange: EventEmitter<ExerciseSessionOptionsLearnFlashcardsDto> = new EventEmitter();

  form: FormGroup;
  availableAnswerLanguages: Array<ExerciseSessionOptionsLanguageOptionDto> = [];
  private sub: Subscription | undefined = undefined;
  private displayNames: Intl.DisplayNames;

  constructor(private fb: FormBuilder, @Inject(LOCALE_ID) private locale: string) {
    this.form = this.fb.group({
      multipleChoiceAnswerLanguageIds: this.fb.array([]),
      writingAnswerLanguageIds: this.fb.array([]),
      targetLanguageId: [''],
      includeReviewExercises: [false],
      includeMultipleChoiceExercises: [false],
      initiallyHideChoices: [false],
      includeWritingExercises: [false],
      includeListeningExercises: [false],
      includePronunciationExercises: [false],
      retypeCorrectAnswerEnabled: [false],
      shuffleFlashcardsEnabled: [false],
      textToSpeechEnabled: [false]
    }, { validators: this.atLeastOneExerciseTypeValidator });
    this.displayNames = new Intl.DisplayNames([this.locale], { type: 'language' });
  }

  ngOnInit(): void {
    // subscribe to form changes and emit staged options
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

  // helpers to expose form arrays in template
  get multipleChoiceAnswerLanguageIds(): FormArray {
    return this.form.get('multipleChoiceAnswerLanguageIds') as FormArray;
  }

  get writingAnswerLanguageIds(): FormArray {
    return this.form.get('writingAnswerLanguageIds') as FormArray;
  }

  private patchFormFromOptions() {
    const options = this.options || ({} as ExerciseSessionOptionsLearnFlashcardsDto);

    this.availableAnswerLanguages = options.availableAnswerLanguages || [];

    // Rebuild multipleChoiceAnswerLanguageIds form array
    const mcLangArray = this.form.get('multipleChoiceAnswerLanguageIds') as FormArray;
    mcLangArray.clear({ emitEvent: false });
    this.availableAnswerLanguages.forEach(lang => {
      const langId = lang.id || '';
      const isSelected = options.multipleChoiceAnswerLanguageIds?.includes(langId) || false;
      mcLangArray.push(this.fb.control(isSelected), { emitEvent: false });
    });

    // Rebuild writingAnswerLanguageIds form array
    const writingLangArray = this.form.get('writingAnswerLanguageIds') as FormArray;
    writingLangArray.clear({ emitEvent: false });
    this.availableAnswerLanguages.forEach(lang => {
      const langId = lang.id || '';
      const isSelected = options.writingAnswerLanguageIds?.includes(langId) || false;
      writingLangArray.push(this.fb.control(isSelected), { emitEvent: false });
    });

    this.form.patchValue({
      targetLanguageId: options.targetLanguageId || '',
      includeReviewExercises: options.includeReviewExercises || false,
      includeMultipleChoiceExercises: options.includeMultipleChoiceExercises || false,
      includeWritingExercises: options.includeWritingExercises || false,
      includeListeningExercises: options.includeListeningExercises || false,
      includePronunciationExercises: options.includePronunciationExercises || false,
      initiallyHideChoices: options.initiallyHideChoices || false,
      retypeCorrectAnswerEnabled: options.retypeCorrectAnswerEnabled || false,
      shuffleFlashcardsEnabled: options.shuffleFlashcardsEnabled || false,
      textToSpeechEnabled: options.textToSpeechEnabled || false
    }, { emitEvent: false });
  }

  private buildDtoFromForm(): ExerciseSessionOptionsLearnFlashcardsDto {
    const mcLangArray = this.form.get('multipleChoiceAnswerLanguageIds') as FormArray;
    const selectedMcLangs = this.availableAnswerLanguages.filter((_, i) => {
      const control = mcLangArray.at(i);
      return control && control.value;
    });

    const writingLangArray = this.form.get('writingAnswerLanguageIds') as FormArray;
    const selectedWritingLangs = this.availableAnswerLanguages.filter((_, i) => {
      const control = writingLangArray.at(i);
      return control && control.value;
    });

    return {
      type: this.options?.type,
      targetLanguageId: this.form.get('targetLanguageId')?.value || undefined,
      multipleChoiceAnswerLanguageIds: selectedMcLangs.map(lang => lang.id),
      writingAnswerLanguageIds: selectedWritingLangs.map(lang => lang.id),
      initiallyHideChoices: !!this.form.get('initiallyHideChoices')?.value,
      includeReviewExercises: !!this.form.get('includeReviewExercises')?.value,
      includeMultipleChoiceExercises: !!this.form.get('includeMultipleChoiceExercises')?.value,
      includeWritingExercises: !!this.form.get('includeWritingExercises')?.value,
      includeListeningExercises: !!this.form.get('includeListeningExercises')?.value,
      includePronunciationExercises: !!this.form.get('includePronunciationExercises')?.value,
      retypeCorrectAnswerEnabled: !!this.form.get('retypeCorrectAnswerEnabled')?.value,
      shuffleFlashcardsEnabled: !!this.form.get('shuffleFlashcardsEnabled')?.value,
      textToSpeechEnabled: !!this.form.get('textToSpeechEnabled')?.value
    } as ExerciseSessionOptionsLearnFlashcardsDto;
  }

  public formatLanguageName(language: ExerciseSessionOptionsLanguageOptionDto) {
    if (language.isoCode) {
      return this.displayNames.of(language.isoCode) || 'N/A';
    }
    return 'N/A';
  }

  private atLeastOneExerciseTypeValidator(control: AbstractControl): ValidationErrors | null {
    const group = control as FormGroup;
    const hasAtLeastOne = 
      group.get('includeReviewExercises')?.value ||
      group.get('includeMultipleChoiceExercises')?.value ||
      group.get('includeWritingExercises')?.value ||
      group.get('includeListeningExercises')?.value ||
      group.get('includePronunciationExercises')?.value;
    return hasAtLeastOne ? null : { atLeastOneExerciseTypeRequired: true };
  }
}

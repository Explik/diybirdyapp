
import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnDestroy, Inject, LOCALE_ID } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, AbstractControl, ValidationErrors } from '@angular/forms';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseSessionOptionsLanguageOptionDto, ExerciseSessionOptionsLearnFlashcardsDto, ExerciseTypeOption } from '../../../../shared/api-client';
import { SessionOptionsComponent } from '../../models/component.interface';
import { Subscription } from 'rxjs';
import { FormErrorComponent } from "../../../../shared/components/form-error/form-error.component";

@Component({
  selector: 'app-session-options-learn-flashcard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormFieldComponent, LabelComponent, SlideToogleComponent, FormErrorComponent, FormErrorComponent],
  templateUrl: './session-options-learn-flashcard.component.html'
})
export class SessionOptionsLearnFlashcardComponent implements OnInit, OnChanges, OnDestroy, SessionOptionsComponent<ExerciseSessionOptionsLearnFlashcardsDto> {
  @Input() options?: ExerciseSessionOptionsLearnFlashcardsDto;
  @Output() optionsChange: EventEmitter<ExerciseSessionOptionsLearnFlashcardsDto> = new EventEmitter();

  form: FormGroup;
  availableAnswerLanguages: Array<ExerciseSessionOptionsLanguageOptionDto> = [];
  availableExerciseTypes: Array<ExerciseTypeOption> = [];
  private sub: Subscription | undefined = undefined;
  private displayNames: Intl.DisplayNames;

  constructor(private fb: FormBuilder, @Inject(LOCALE_ID) private locale: string) {
    this.form = this.fb.group({
      answerLanguageIds: this.fb.array([], this.atLeastOneSelectedValidator),
      exerciseTypeIds: this.fb.array([]),
      retypeCorrectAnswerEnabled: [false],
      textToSpeechEnabled: [false]
    });
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

  // helper to expose form array in template
  get answerLanguageIds(): FormArray {
    return this.form.get('answerLanguageIds') as FormArray;
  }

  get exerciseTypeIds(): FormArray {
    return this.form.get('exerciseTypeIds') as FormArray;
  }

  private patchFormFromOptions() {
    const options = this.options || ({} as ExerciseSessionOptionsLearnFlashcardsDto);

    this.availableAnswerLanguages = options.availableAnswerLanguages || [];
    this.availableExerciseTypes = options.availableExerciseTypes || [];

    // Rebuild answerLanguageIds form array
    const answerLangArray = this.form.get('answerLanguageIds') as FormArray;
    answerLangArray.clear({ emitEvent: false });
    this.availableAnswerLanguages.forEach(lang => {
      const langId = lang.id || '';
      const isSelected = options.answerLanguageIds?.includes(langId) || false;
      answerLangArray.push(this.fb.control(isSelected), { emitEvent: false });
    });

    // Rebuild exerciseTypeIds form array
    const exerciseTypeArray = this.form.get('exerciseTypeIds') as FormArray;
    exerciseTypeArray.clear({ emitEvent: false });
    this.availableExerciseTypes.forEach(type => {
      const isSelected = options.exerciseTypesIds?.includes(type.id || '') || false;
      exerciseTypeArray.push(this.fb.control(isSelected), { emitEvent: false });
    });

    this.form.patchValue({
      retypeCorrectAnswerEnabled: options.retypeCorrectAnswerEnabled || false,
      textToSpeechEnabled: options.textToSpeechEnabled || false
    }, { emitEvent: false });
  }

  private buildDtoFromForm(): ExerciseSessionOptionsLearnFlashcardsDto {
    const answerLangArray = this.form.get('answerLanguageIds') as FormArray;
    const selectedLangs = this.availableAnswerLanguages.filter((_, i) => {
      const control = answerLangArray.at(i);
      return control && control.value;
    });

    const exerciseTypeArray = this.form.get('exerciseTypeIds') as FormArray;
    const selectedExerciseTypes = this.availableExerciseTypes
      .filter((_, i) => {
        const control = exerciseTypeArray.at(i);
        return control && control.value;
      })
      .map(type => type.id || '');

    return {
      type: this.options?.type,
      answerLanguageIds: selectedLangs.map(lang => lang.id),
      exerciseTypesIds: selectedExerciseTypes,
      retypeCorrectAnswerEnabled: !!this.form.get('retypeCorrectAnswerEnabled')?.value,
      textToSpeechEnabled: !!this.form.get('textToSpeechEnabled')?.value
    } as ExerciseSessionOptionsLearnFlashcardsDto;
  }

  public formatLanguageName(language: ExerciseSessionOptionsLanguageOptionDto) {
    if (language.isoCode) {
      return this.displayNames.of(language.isoCode) || 'N/A';
    }
    return 'N/A';
  }

  private atLeastOneSelectedValidator(control: AbstractControl): ValidationErrors | null {
    const formArray = control as FormArray;
    const hasAtLeastOne = formArray.controls.some(ctrl => ctrl.value === true);
    return hasAtLeastOne ? null : { atLeastOneRequired: true };
  }
}

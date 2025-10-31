
import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray } from '@angular/forms';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseSessionOptionsLearnFlashcardsDto } from '../../../../shared/api-client';
import { SessionOptionsComponent } from '../../models/component.interface';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-session-options-learn-flashcard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormFieldComponent, LabelComponent, SlideToogleComponent],
  templateUrl: './session-options-learn-flashcard.component.html'
})
export class SessionOptionsLearnFlashcardComponent implements OnInit, OnChanges, OnDestroy, SessionOptionsComponent<ExerciseSessionOptionsLearnFlashcardsDto> {
  @Input() options?: ExerciseSessionOptionsLearnFlashcardsDto;
  @Output() optionsChange: EventEmitter<ExerciseSessionOptionsLearnFlashcardsDto> = new EventEmitter();

  form: FormGroup;
  availableAnswerLanguages: Array<string> = [];
  private sub: Subscription | undefined = undefined;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      answerLanguageIds: this.fb.array([]),
      retypeCorrectAnswerEnabled: [false],
      textToSpeechEnabled: [false]
    });
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

  private patchFormFromOptions() {
    const options = this.options || ({} as ExerciseSessionOptionsLearnFlashcardsDto);

    this.availableAnswerLanguages = options.availableAnswerLanguageIds || [];

    this.form.patchValue({
      retypeCorrectAnswerEnabled: options.retypeCorrectAnswerEnabled || false,
      textToSpeechEnabled: options.textToSpeechEnabled || false
    }, { emitEvent: false });
  }

  private buildDtoFromForm(): ExerciseSessionOptionsLearnFlashcardsDto {
    const arr = this.form.get('answerLanguageIds') as FormArray;
    const selectedLangs = this.availableAnswerLanguages.filter((_, i) => arr.at(i).value);

    return {
      type: this.options?.type,
      answerLanguageIds: selectedLangs,
      retypeCorrectAnswerEnabled: !!this.form.get('retypeCorrectAnswerEnabled')?.value,
      textToSpeechEnabled: !!this.form.get('textToSpeechEnabled')?.value
    } as ExerciseSessionOptionsLearnFlashcardsDto;
  }
}

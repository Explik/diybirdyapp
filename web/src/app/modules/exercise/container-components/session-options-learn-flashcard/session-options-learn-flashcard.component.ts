
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray } from '@angular/forms';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseSessionOptionsLearnFlashcardsDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-session-options-learn-flashcard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormFieldComponent, LabelComponent, SlideToogleComponent],
  templateUrl: './session-options-learn-flashcard.component.html'
})
export class SessionOptionsLearnFlashcardComponent implements OnInit {
  form: FormGroup;
  availableAnswerLanguages: Array<string> = [];

  constructor(private fb: FormBuilder, private exerciseService: ExerciseService) {
    this.form = this.fb.group({
      answerLanguageIds: this.fb.array([]),
      retypeCorrectAnswerEnabled: [false],
      textToSpeechEnabled: [false]
    });
  }

  ngOnInit(): void {
    this.exerciseService.getExerciseSessionOptions().subscribe(data => {
      if (!data) return;

      let options = <ExerciseSessionOptionsLearnFlashcardsDto><any>data || {};
      this.availableAnswerLanguages = options.availableAnswerLanguageIds || [];

      const arr = this.form.get('answerLanguageIds') as FormArray;
      arr.clear();
      const selected = new Set(options.answerLanguageIds || []);
      this.availableAnswerLanguages.forEach(lang => {
        arr.push(this.fb.control(selected.has(lang)));
      });

      this.form.patchValue({
        retypeCorrectAnswerEnabled: options.retypeCorrectAnswerEnabled || false,
        textToSpeechEnabled: options.textToSpeechEnabled || false
      });
    });
  }

  // helper to expose form array in template
  get answerLanguageIds(): FormArray {
    return this.form.get('answerLanguageIds') as FormArray;
  }
}

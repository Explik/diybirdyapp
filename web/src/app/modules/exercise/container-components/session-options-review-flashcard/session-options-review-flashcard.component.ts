import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { SelectComponent } from '../../../../shared/components/select/select.component';
import { OptionComponent } from '../../../../shared/components/option/option.component';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseSessionOptionsReviewFlashcardsDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-session-options-review-flashcard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SelectComponent, OptionComponent, FormFieldComponent, LabelComponent, SlideToogleComponent],
  templateUrl: './session-options-review-flashcard.component.html'
})
export class SessionOptionsReviewFlashcardComponent implements OnInit {
  form: FormGroup;
  availableLanguages: Array<string> = [];

  constructor(private fb: FormBuilder, private exerciseService: ExerciseService) {
    this.form = this.fb.group({
      initialFlashcardLanguageId: [''],
      textToSpeechEnabled: [false]
    });
  }

  ngOnInit(): void {
    this.exerciseService.getExerciseSessionOptions().subscribe(data => {
      if (!data) return;

      let options = <ExerciseSessionOptionsReviewFlashcardsDto><any>data || {};
      this.availableLanguages = options.availableFlashcardLanguageIds || [];

      this.form.patchValue({
        initialFlashcardLanguageId: options.initialFlashcardLanguageId || '',
        textToSpeechEnabled: options.textToSpeechEnabled || false
      });
    });
  }
}

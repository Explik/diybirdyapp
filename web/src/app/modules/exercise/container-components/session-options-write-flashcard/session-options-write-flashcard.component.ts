
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { SelectComponent } from '../../../../shared/components/select/select.component';
import { OptionComponent } from '../../../../shared/components/option/option.component';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { SlideToogleComponent } from "../../../../shared/components/slide-toogle/slide-toogle.component";
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseSessionOptionsWriteFlashcardsDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-session-options-write-flashcard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SelectComponent, OptionComponent, FormFieldComponent, LabelComponent, SlideToogleComponent],
  templateUrl: './session-options-write-flashcard.component.html'
})
export class SessionOptionsWriteFlashcardComponent implements OnInit {
  form: FormGroup;
  availableAnswerLanguages: Array<string> = [];

  constructor(private fb: FormBuilder, private exerciseService: ExerciseService) {
    this.form = this.fb.group({
      answerLanguageId: [''],
      retypeCorrectAnswerEnabled: [false],
      textToSpeechEnabled: [false]
    });
  }

  ngOnInit(): void {
    this.exerciseService.getExerciseSessionOptions().subscribe(data => {
      if (!data) return;

      let options = <ExerciseSessionOptionsWriteFlashcardsDto><any>data || {};
      this.availableAnswerLanguages = options.availableAnswerLanguageIds || [];

      this.form.patchValue({
        answerLanguageId: options.answerLanguageId || '',
        retypeCorrectAnswerEnabled: options.retypeCorrectAnswerEnabled || false,
        textToSpeechEnabled: options.textToSpeechEnabled || false
      });
    });
  }
}

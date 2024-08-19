import { Injectable, Type } from '@angular/core';
import { ExerciseService } from './exercise.service';
import { map, Observable, of } from 'rxjs';
import { ExerciseComponent } from '../models/exerciseComponent.interface';
import { InfoBoxComponent } from '../components/info-box/info-box.component';
import { InstructionComponent } from '../components/instruction/instruction.component';
import { CorrectableTextFieldComponent } from '../../../shared/components/correctable-text-field/correctable-text-field.component';
import { BaseExercise, MultipleChoiceExerciseAnswer, MultipleTextChoiceOptionExercise, TranslateSentenceExercise, WriteSentenceUsingWordExercise } from '../models/exercise.interface';
import { TextQuoteComponent } from '../../../shared/components/text-quote/text-quote.component';
import { SelectOptionFieldComponent } from '../components/select-option-field/select-option-field.component';

@Injectable({
  providedIn: 'root'
})
export class ExerciseComponentService {
  constructor(private exerciseService: ExerciseService) { }

  getExerciseComponents(id: string) : Observable<ExerciseComponent[]> {
    return this.exerciseService.getExercise(id).pipe(map(this.mapExerciseComponents))
  }

  private mapExerciseComponents(baseExercise: BaseExercise): ExerciseComponent[] {
    if (baseExercise.exerciseType == "write-translated-sentence-exercise") {
      const exercise = <TranslateSentenceExercise>baseExercise;
      return [
        {
          component: InstructionComponent, 
          inputs: { 
              title: "Translate the sentence",
              instruction: `Translate the sentence below into ${exercise.targetLanguage}`
          }
        },
        {
          component: TextQuoteComponent,
          inputs: {
            text: exercise.originalSentence
          }
        },
        {
          component: CorrectableTextFieldComponent,
          inputs: {
            state: "input"
          }
        }
      ];
    }
    else if (baseExercise.exerciseType == 'write-sentence-using-word-exercise') {
      const exercise = <WriteSentenceUsingWordExercise>baseExercise;
      return [
        {
          component: InstructionComponent, 
          inputs: { 
              title: "Write an original sentence",
              instruction: `Write a sentence using the word \"${exercise.word}\"`
          }
        },
        {
          component: CorrectableTextFieldComponent,
          inputs: {
            state: "input"
          }
        }
      ];
    }
    else if (baseExercise.exerciseType == "multiple-text-choice-exercise") {
      const exercise = <MultipleTextChoiceOptionExercise>baseExercise;
      return [
        {
          component: InstructionComponent, 
          inputs: { 
              title: "Pick the correct option",
              instruction: "What is the translation of dog?"
          }
        },
        {
          component: SelectOptionFieldComponent,
          inputs: {
            state: "input",
            options: exercise.options
          }
        }
      ]
    } 
    return [];
  }
}
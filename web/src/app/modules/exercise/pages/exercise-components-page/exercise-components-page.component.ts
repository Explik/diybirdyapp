import { Component } from '@angular/core';
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { ExerciseInputWriteTextComponent } from "../../components/exercise-input-write-text/exercise-input-write-text.component";
import { Exercise } from '../../models/exercise.interface';
import { ExerciseInputSelectOptionsComponent } from '../../components/exercise-input-select-options/exercise-input-select-options.component';
import { ExerciseInputRecordAudioComponent } from "../../components/exercise-input-record-audio/exercise-input-record-audio.component";
import { ExerciseInputArrangeTextOptionsComponent } from "../../components/exercise-input-arrange-text-options/exercise-input-arrange-text-options.component";
import { ExerciseInputWritePlaceholdersComponent } from "../../components/exercise-input-write-placeholders/exercise-input-write-placeholders.component";

@Component({
  selector: 'app-exercise-components-page',
  imports: [ExerciseInputWriteTextComponent, ExerciseInputSelectOptionsComponent, ExerciseInputRecordAudioComponent, ExerciseInputArrangeTextOptionsComponent, ExerciseInputWritePlaceholdersComponent],
  templateUrl: './exercise-components-page.component.html'
})
export class ExerciseComponentsPageComponent {
  constructor() { }

  audioValue: ExerciseInputAudioDto = {
    id: '1',
    type: 'audio',
    url: ''
  }

  arrangeTextValue: ExerciseInputArrangeTextOptionsDto = {
    id: '1',
    type: 'arrange-text',
    options: [
      { id: '1', text: 'a' }, 
      { id: '2', text: 'This' }, 
      { id: '3', text: 'is' },
      { id: '4', text: 'test' }
    ],
    value: '2'
  };

  textValue: ExerciseInputTextDto = {
    id: '1',
    type: 'text',
    text: 'Hello, World!',
    feedback: undefined
  };

  textValueWithCorrectFeedback: ExerciseInputTextDto = { 
    id: '1',
    type: 'text',
    text: 'Hello, World!', 
    feedback: { correctValues: ['Hello, World!'], incorrectValues: [] } 
  };

  textValueWithIncorrectFeedback: ExerciseInputTextDto = { 
    id: '1',
    type: 'text',
    text: 'Hello, ', 
    feedback: { correctValues: ['Hello, world!'], incorrectValues: ['Goodbye, World!'] } 
  };

  multipleChoiceValue: ExerciseInputMultipleChoiceTextDto = {
    id: '1',
    type: 'multiple-choice',
    options: [
      { id: '1', text: 'Option 1' },
      { id: '2', text: 'Option 2' },
      { id: '3', text: 'Option 3' },
      { id: '4', text: 'Option 4' }
    ],
    value: "1",
  };

  multipleChoiceValueWithFeedback: ExerciseInputMultipleChoiceTextDto = { 
    id: '1',
    type: 'multiple-choice',
    options: [
      { id: '1', text: 'Option 1' },
      { id: '2', text: 'Option 2' },
      { id: '3', text: 'Option 3' },
      { id: '4', text: 'Option 4' }
    ],
    feedback: {
      correctOptionIds: ['1'],
      incorrectOptionIds: ['2', '3', '4']
    },
    value: '1'
  };
}

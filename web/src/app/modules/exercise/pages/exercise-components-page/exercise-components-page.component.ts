import { Component } from '@angular/core';
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { ExerciseInputWriteTextComponent } from "../../components/exercise-input-write-text/exercise-input-write-text.component";
import { Exercise } from '../../models/exercise.interface';
import { ExerciseInputSelectOptionsComponent } from '../../components/exercise-input-select-options/exercise-input-select-options.component';
import { ExerciseInputRecordAudioComponent } from "../../components/exercise-input-record-audio/exercise-input-record-audio.component";
import { ExerciseInputArrangeTextOptionsComponent } from "../../components/exercise-input-arrange-text-options/exercise-input-arrange-text-options.component";
import { ExerciseInputWritePlaceholdersComponent } from "../../components/exercise-input-write-placeholders/exercise-input-write-placeholders.component";
import { ExerciseInputSelectPlaceholdersComponent } from "../../components/exercise-input-select-placeholders/exercise-input-select-placeholders.component";
import { ExerciseInputPairOptionsComponent } from '../../components/exercise-input-pair-options/exercise-input-pair-options.component';
import { ExerciseInputRecordVideoComponent } from "../../components/exercise-input-record-video/exercise-input-record-video.component";
import { ExerciseInputArrangeTextOptionsDto, ExerciseInputPairOptionsDto, ExerciseInputRecordAudioDto, ExerciseInputRecordVideoDto, ExerciseInputSelectOptionsDto, ExerciseInputSelectPlaceholdersDto, ExerciseInputWritePlaceholdersDto, ExerciseInputWriteTextDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-components-page',
  imports: [ExerciseInputWriteTextComponent, ExerciseInputSelectOptionsComponent, ExerciseInputRecordAudioComponent, ExerciseInputArrangeTextOptionsComponent, ExerciseInputWritePlaceholdersComponent, ExerciseInputSelectPlaceholdersComponent, ExerciseInputPairOptionsComponent, ExerciseInputRecordVideoComponent],
  templateUrl: './exercise-components-page.component.html'
})
export class ExerciseComponentsPageComponent {
  constructor() { }

  audioValue: ExerciseInputRecordAudioDto = {
    id: '1',
    type: 'audio',
    url: ''
  }

  videoValue: ExerciseInputRecordVideoDto = {
    id: '1',
    type: 'video',
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

  textValue: ExerciseInputWriteTextDto = {
    id: '1',
    type: 'text',
    text: 'Hello, World!',
    feedback: undefined
  };

  textValueWithCorrectFeedback: ExerciseInputWriteTextDto = { 
    id: '1',
    type: 'text',
    text: 'Hello, World!', 
    feedback: { correctValues: ['Hello, World!'], incorrectValues: [] } 
  };

  textValueWithIncorrectFeedback: ExerciseInputWriteTextDto = { 
    id: '1',
    type: 'text',
    text: 'Hello, ', 
    feedback: { correctValues: ['Hello, world!'], incorrectValues: ['Goodbye, World!'] } 
  };

  writePlaceholdersValue: ExerciseInputWritePlaceholdersDto = {
    id: '1',
    type: 'write-placeholders',
    parts: [
      { id: '1', type: 'text', value: 'This is a' },
      { id: '2', type: 'placeholder', value: '', size: 100 },
      { id: '3', type: 'text', value: 'blue' },
      { id: '4', type: 'placeholder', value: '', size: 100 },
      { id: '5', type: 'text', value: 'car.' }
    ],
    feedback: undefined
  }

  writePlaceholdersValueWithFeedback: ExerciseInputWritePlaceholdersDto = {
    id: '1',
    type: 'write-placeholders',
    parts: [
      { id: '1', type: 'text', value: 'This is a' },
      { id: '2', type: 'placeholder', value: 'big', size: 100 },
      { id: '3', type: 'text', value: 'blue' },
      { id: '4', type: 'placeholder', value: 'red', size: 100 },
      { id: '5', type: 'text', value: 'car.' }
    ],
    feedback: { 
      correctPlaceholdersIds: ['2'], 
      incorrectPlaceholdersIds: ['4'] 
    }
  }

  selectTextOptionsValue: ExerciseInputSelectOptionsDto = {
    id: '1',
    type: 'multiple-choice',
    optionType: 'text',
    options: [
      { id: '1', text: 'Option 1' },
      { id: '2', text: 'Option 2' },
      { id: '3', text: 'Option 3' },
      { id: '4', text: 'Option 4' }
    ],
    value: "1",
  };

  selectTextOptionsValueWithFeedback: ExerciseInputSelectOptionsDto = { 
    id: '1',
    type: 'multiple-choice',
    optionType: 'text',
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

  selectAudioOptionsValue: ExerciseInputSelectOptionsDto = {
    id: '1',
    type: 'multiple-choice',
    optionType: 'audio',
    options: [
      { id: '1', text: 'Option 1', url: 'https://download.samplelib.com/mp3/sample-3s.mp3' },
      { id: '2', text: 'Option 2', url: 'https://download.samplelib.com/mp3/sample-6s.mp3' },
      { id: '3', text: 'Option 3', url: 'https://download.samplelib.com/mp3/sample-9s.mp3' },
      { id: '4', text: 'Option 4', url: 'https://download.samplelib.com/mp3/sample-12s.mp3' }
    ],
    value: "1",
  };

  selectAudioOptionsValueWithFeedback: ExerciseInputSelectOptionsDto = { 
    id: '1',
    type: 'multiple-choice',
    optionType: 'audio',
    options: [
      { id: '1', text: 'Option 1', url: 'https://download.samplelib.com/mp3/sample-3s.mp3' },
      { id: '2', text: 'Option 2', url: 'https://download.samplelib.com/mp3/sample-6s.mp3' },
      { id: '3', text: 'Option 3', url: 'https://download.samplelib.com/mp3/sample-9s.mp3' },
      { id: '4', text: 'Option 4', url: 'https://download.samplelib.com/mp3/sample-12s.mp3' }
    ],
    feedback: {
      correctOptionIds: ['1'],
      incorrectOptionIds: ['2', '3', '4']
    },
    value: '1'
  };

  selectImageOptionsValue: ExerciseInputSelectOptionsDto = {
    id: '1',
    type: 'multiple-choice',
    optionType: 'image',
    options: [
      { id: '1', text: 'Option 1', url: 'https://picsum.photos/id/21/200/300' },
      { id: '2', text: 'Option 2', url: 'https://picsum.photos/id/22/200/300' },
      { id: '3', text: 'Option 3', url: 'https://picsum.photos/id/23/200/300' },
      { id: '4', text: 'Option 4', url: 'https://picsum.photos/id/24/200/300' }
    ],
    value: "1",
  };

  selectImageOptionsValueWithFeedback: ExerciseInputSelectOptionsDto = {
    id: '1',
    type: 'multiple-choice',
    optionType: 'image',
    options: [
      { id: '1', text: 'Option 1', url: 'https://picsum.photos/id/21/200/300' },
      { id: '2', text: 'Option 2', url: 'https://picsum.photos/id/22/200/300' },
      { id: '3', text: 'Option 3', url: 'https://picsum.photos/id/23/200/300' },
      { id: '4', text: 'Option 4', url: 'https://picsum.photos/id/24/200/300' }
    ],
    feedback: {
      correctOptionIds: ['1'],
      incorrectOptionIds: ['2', '3', '4']
    },
    value: '1'
  };

  selectVideoOptionsValue: ExerciseInputSelectOptionsDto = {
    id: '1',
    type: 'multiple-choice',
    optionType: 'video',
    options: [
      { id: '1', text: 'Option 1', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4' },
      { id: '2', text: 'Option 2', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4' },
      { id: '3', text: 'Option 3', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4' },
      { id: '4', text: 'Option 4', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4' }
    ],
    value: "1",
  };

  selectVideoOptionsValueWithFeedback: ExerciseInputSelectOptionsDto = {
    id: '1',
    type: 'multiple-choice',
    optionType: 'video',
    options: [
      { id: '1', text: 'Option 1', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4' },
      { id: '2', text: 'Option 2', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4' },
      { id: '3', text: 'Option 3', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4' },
      { id: '4', text: 'Option 4', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4' }
    ],
    feedback: {
      correctOptionIds: ['1'],
      incorrectOptionIds: ['2', '3', '4']
    },
    value: '1'
  };

  selectPlaceholdersValue: ExerciseInputSelectPlaceholdersDto = {
    id: '1',
    type: 'select-placeholders',
    parts: [
      { type: 'text', value: 'This is a' },
      { type: 'placeholder'}
    ],
    options: [
      { id: '1', text: 'test' },
      { id: '2', text: 'example' }
    ]
  }

  pairOptionsValue: ExerciseInputPairOptionsDto = {
    id: '1',
    type: 'pair-options',
    leftOptionType: 'text',
    rightOptionType: 'text',
    leftOptions: [
      { id: '1', text: 'Apple' }, 
      { id: '2', text: 'Banana' }, 
      { id: '3', text: 'Orange' }
    ] as any[],
    rightOptions: [
      { id: '4', text: 'Red' }, 
      { id: '5', text: 'Yellow' }, 
      { id: '6', text: 'Orange' }
    ] as any[],
  }

  pairOptionsValueWithCorrectFeedback: ExerciseInputPairOptionsDto = {
    id: '1',
    type: 'pair-options',
    leftOptionType: 'text',
    rightOptionType: 'text',
    leftOptions: [
      { id: '1', text: 'Apple' }, 
      { id: '2', text: 'Banana' }, 
      { id: '3', text: 'Orange' }
    ] as any[],
    rightOptions: [
      { id: 'a', text: 'Red' }, 
      { id: 'b', text: 'Yellow' }, 
      { id: 'c', text: 'Orange' }
    ] as any[],
    feedback: {
      correctPairs: [{ leftId: '2', rightId: 'a' }],
      incorrectPairs: []
    }
  }

  pairOptionsValueWithIncorrectFeedback: ExerciseInputPairOptionsDto = {
    id: '1',
    type: 'pair-options',
    leftOptionType: 'text',
    rightOptionType: 'text',
    leftOptions: [
      { id: '1', text: 'Apple' }, 
      { id: '2', text: 'Banana' }, 
      { id: '3', text: 'Orange' }
    ] as any[],
    rightOptions: [
      { id: 'a', text: 'Red' }, 
      { id: 'b', text: 'Yellow' }, 
      { id: 'c', text: 'Orange' }
    ] as any[],
    feedback: {
      correctPairs: [],
      incorrectPairs: [{ leftId: '1', rightId: 'b' }]
    }
  }
}

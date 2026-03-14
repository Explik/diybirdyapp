import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { EMPTY } from 'rxjs';

import { DynamicFlashcardContentComponent } from './dynamic-flashcard-content.component';
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { ExerciseContentAudioDto, ExerciseContentFlashcardDto, ExerciseContentTextDto } from '../../../../shared/api-client';
import { FlashcardComponent } from '../../../../shared/components/flashcard/flashcard.component';
import { HotkeyService } from '../../../../shared/services/hotKey.service';

describe('DynamicFlashcardContentComponent', () => {
  const expectJasmine = expect as unknown as <T>(actual: T) => jasmine.Matchers<T>;

  let component: DynamicFlashcardContentComponent;
  let fixture: ComponentFixture<DynamicFlashcardContentComponent>;
  let audioPlayService: jasmine.SpyObj<AudioPlayService>;

  function createTextContent(pronunciationUrl: string): ExerciseContentTextDto {
    return {
      id: 'front-text',
      type: 'text-content',
      text: 'Front text',
      pronunciationUrl,
    };
  }

  function createAudioContent(audioUrl: string): ExerciseContentAudioDto {
    return {
      id: 'back-audio',
      type: 'audio-content',
      audioUrl,
    };
  }

  function createFlashcardData(initialSide: 'front' | 'back' = 'front'): ExerciseContentFlashcardDto {
    return {
      id: 'flashcard-1',
      type: 'flashcard',
      front: createTextContent('front-pronunciation.mp3'),
      back: createAudioContent('back-audio.mp3'),
      initialSide,
    };
  }

  beforeEach(async () => {
    audioPlayService = jasmine.createSpyObj<AudioPlayService>('AudioPlayService', ['play', 'stop', 'toggle']);

    await TestBed.configureTestingModule({
      imports: [DynamicFlashcardContentComponent],
      providers: [
        { provide: AudioPlayService, useValue: audioPlayService },
        {
          provide: HotkeyService,
          useValue: {
            onHotkey: jasmine.createSpy('onHotkey').and.returnValue(EMPTY),
          },
        },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicFlashcardContentComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();

    expectJasmine(component).toBeTruthy();
  });

  it('autoplays the initial side when the flashcard appears', () => {
    component.data = createFlashcardData('front');

    fixture.detectChanges();

    expectJasmine(audioPlayService.play).toHaveBeenCalledWith('front-pronunciation.mp3');
  });

  it('stops the previous side and starts the new side when flipped', () => {
    component.data = createFlashcardData('front');
    fixture.detectChanges();

    audioPlayService.play.calls.reset();
    audioPlayService.stop.calls.reset();

    const flashcard = fixture.debugElement.query(By.directive(FlashcardComponent)).componentInstance as FlashcardComponent;
    flashcard.flip();
    fixture.detectChanges();

    expectJasmine(audioPlayService.stop).toHaveBeenCalledWith('front-pronunciation.mp3');
    expectJasmine(audioPlayService.play).toHaveBeenCalledWith('back-audio.mp3');
  });

  it('does not autoplay when autoplay is disabled', () => {
    component.autoplay = false;
    component.data = createFlashcardData('front');

    fixture.detectChanges();

    expectJasmine(audioPlayService.play).not.toHaveBeenCalled();
  });
});

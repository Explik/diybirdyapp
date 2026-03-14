import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicAudioContentComponent } from './dynamic-audio-content.component';
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { ExerciseContentAudioDto } from '../../../../shared/api-client';

describe('DynamicAudioContentComponent', () => {
  const expectJasmine = expect as unknown as <T>(actual: T) => jasmine.Matchers<T>;

  let component: DynamicAudioContentComponent;
  let fixture: ComponentFixture<DynamicAudioContentComponent>;
  let audioPlayService: jasmine.SpyObj<AudioPlayService>;

  function createAudioData(audioUrl: string = 'audio.mp3'): ExerciseContentAudioDto {
    return {
      id: 'audio-1',
      type: 'audio-content',
      audioUrl,
    };
  }

  beforeEach(async () => {
    audioPlayService = jasmine.createSpyObj<AudioPlayService>('AudioPlayService', ['play', 'stop', 'toggle']);

    await TestBed.configureTestingModule({
      imports: [DynamicAudioContentComponent],
      providers: [
        { provide: AudioPlayService, useValue: audioPlayService },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicAudioContentComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();

    expectJasmine(component).toBeTruthy();
  });

  it('starts playing audio on appearance when autoplay is enabled', () => {
    component.data = createAudioData('intro.mp3');

    fixture.detectChanges();

    expectJasmine(audioPlayService.play).toHaveBeenCalledWith('intro.mp3');
  });

  it('does not autoplay when autoplay is disabled', () => {
    component.autoplay = false;
    component.data = createAudioData('intro.mp3');

    fixture.detectChanges();

    expectJasmine(audioPlayService.play).not.toHaveBeenCalled();
  });

  it('stops the current audio when autoplay is turned off', () => {
    component.data = createAudioData('intro.mp3');
    fixture.detectChanges();

    audioPlayService.play.calls.reset();

    component.autoplay = false;
    fixture.detectChanges();

    expectJasmine(audioPlayService.stop).toHaveBeenCalledWith('intro.mp3');
  });
});

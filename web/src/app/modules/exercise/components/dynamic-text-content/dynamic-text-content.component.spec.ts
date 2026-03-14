import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicTextContentComponent } from './dynamic-text-content.component';
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { ExerciseContentTextDto } from '../../../../shared/api-client';

describe('DynamicTextContentComponent', () => {
  const expectJasmine = expect as unknown as <T>(actual: T) => jasmine.Matchers<T>;

  let component: DynamicTextContentComponent;
  let fixture: ComponentFixture<DynamicTextContentComponent>;
  let audioPlayService: jasmine.SpyObj<AudioPlayService>;

  function createTextData(pronunciationUrl?: string): ExerciseContentTextDto {
    return {
      id: 'text-1',
      type: 'text-content',
      text: 'Hello',
      pronunciationUrl,
    };
  }

  beforeEach(async () => {
    audioPlayService = jasmine.createSpyObj<AudioPlayService>('AudioPlayService', ['play', 'stop', 'toggle']);

    await TestBed.configureTestingModule({
      imports: [DynamicTextContentComponent],
      providers: [
        { provide: AudioPlayService, useValue: audioPlayService },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicTextContentComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();

    expectJasmine(component).toBeTruthy();
  });

  it('starts playing pronunciation on appearance when autoplay is enabled', () => {
    component.data = createTextData('pronunciation.mp3');

    fixture.detectChanges();

    expectJasmine(audioPlayService.play).toHaveBeenCalledWith('pronunciation.mp3');
  });

  it('does not autoplay when pronunciation is missing', () => {
    component.data = createTextData();

    fixture.detectChanges();

    expectJasmine(audioPlayService.play).not.toHaveBeenCalled();
  });

  it('stops the current pronunciation when autoplay is turned off', () => {
    component.data = createTextData('pronunciation.mp3');
    fixture.detectChanges();

    audioPlayService.play.calls.reset();

    component.autoplay = false;
    fixture.detectChanges();

    expectJasmine(audioPlayService.stop).toHaveBeenCalledWith('pronunciation.mp3');
  });
});

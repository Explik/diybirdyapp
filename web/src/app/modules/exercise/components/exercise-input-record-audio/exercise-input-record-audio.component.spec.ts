import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseInputRecordAudioComponent } from './exercise-input-record-audio.component';

describe('ExerciseInputRecordAudioComponent', () => {
  let component: ExerciseInputRecordAudioComponent;
  let fixture: ComponentFixture<ExerciseInputRecordAudioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInputRecordAudioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInputRecordAudioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

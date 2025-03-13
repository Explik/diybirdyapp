import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseInputRecordVideoComponent } from './exercise-input-record-video.component';

describe('ExerciseInputRecordVideoComponent', () => {
  let component: ExerciseInputRecordVideoComponent;
  let fixture: ComponentFixture<ExerciseInputRecordVideoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInputRecordVideoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInputRecordVideoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseInputPairOptionsComponent } from './exercise-input-pair-options.component';

describe('ExerciseInputPairOptionsComponent', () => {
  let component: ExerciseInputPairOptionsComponent;
  let fixture: ComponentFixture<ExerciseInputPairOptionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInputPairOptionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInputPairOptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

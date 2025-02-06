import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseInputArrangeTextOptionsComponent } from './exercise-input-arrange-text-options.component';

describe('ExerciseInputArrangeTextOptionsComponent', () => {
  let component: ExerciseInputArrangeTextOptionsComponent;
  let fixture: ComponentFixture<ExerciseInputArrangeTextOptionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInputArrangeTextOptionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInputArrangeTextOptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

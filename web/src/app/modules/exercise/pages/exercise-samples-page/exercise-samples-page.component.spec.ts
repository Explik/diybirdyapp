import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseSamplesPageComponent } from './exercise-samples-page.component';

describe('ExerciseSamplesPageComponent', () => {
  let component: ExerciseSamplesPageComponent;
  let fixture: ComponentFixture<ExerciseSamplesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseSamplesPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseSamplesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

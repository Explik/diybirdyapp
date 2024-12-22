import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseNavigationCheckAnswerContainerComponent } from './exercise-navigation-check-answer-container.component';

describe('ExerciseNavigationCheckAnswerContainerComponent', () => {
  let component: ExerciseNavigationCheckAnswerContainerComponent;
  let fixture: ComponentFixture<ExerciseNavigationCheckAnswerContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseNavigationCheckAnswerContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseNavigationCheckAnswerContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

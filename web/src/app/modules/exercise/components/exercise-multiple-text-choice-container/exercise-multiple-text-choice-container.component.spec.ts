import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseMultipleTextChoiceContainerComponent } from './exercise-multiple-text-choice-container.component';

describe('ExerciseMultipleTextChoiceContainerComponent', () => {
  let component: ExerciseMultipleTextChoiceContainerComponent;
  let fixture: ComponentFixture<ExerciseMultipleTextChoiceContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseMultipleTextChoiceContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseMultipleTextChoiceContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

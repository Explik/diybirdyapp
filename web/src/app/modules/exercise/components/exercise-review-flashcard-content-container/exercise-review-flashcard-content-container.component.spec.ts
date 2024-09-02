import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseReviewFlashcardContentContainerComponent } from './exercise-review-flashcard-content-container.component';

describe('ExerciseReviewFlashcardContentContainerComponent', () => {
  let component: ExerciseReviewFlashcardContentContainerComponent;
  let fixture: ComponentFixture<ExerciseReviewFlashcardContentContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseReviewFlashcardContentContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseReviewFlashcardContentContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

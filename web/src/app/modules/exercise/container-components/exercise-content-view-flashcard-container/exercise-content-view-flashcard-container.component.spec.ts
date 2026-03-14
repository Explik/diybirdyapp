import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseContentViewFlashcardContainerComponent } from './exercise-content-view-flashcard-container.component';

describe('ExerciseContentViewFlashcardContainerComponent', () => {
  let component: ExerciseContentViewFlashcardContainerComponent;
  let fixture: ComponentFixture<ExerciseContentViewFlashcardContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseContentViewFlashcardContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseContentViewFlashcardContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseWriteFlashcardContentContainerComponent } from './exercise-write-flashcard-content-container.component';

describe('ExerciseWriteFlashcardContentContainerComponent', () => {
  let component: ExerciseWriteFlashcardContentContainerComponent;
  let fixture: ComponentFixture<ExerciseWriteFlashcardContentContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseWriteFlashcardContentContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseWriteFlashcardContentContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

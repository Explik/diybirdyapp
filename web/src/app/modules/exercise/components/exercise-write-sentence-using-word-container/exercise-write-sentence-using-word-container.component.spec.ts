import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseWriteSentenceUsingWordContainerComponent } from './exercise-write-sentence-using-word-container.component';

describe('ExerciseWriteSentenceUsingWordContainerComponent', () => {
  let component: ExerciseWriteSentenceUsingWordContainerComponent;
  let fixture: ComponentFixture<ExerciseWriteSentenceUsingWordContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseWriteSentenceUsingWordContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseWriteSentenceUsingWordContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

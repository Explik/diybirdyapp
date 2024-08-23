import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseWriteTranslatedSentenceContainerComponent } from './exercise-write-translated-sentence-container.component';

describe('ExerciseWriteTranslatedSentenceContainerComponent', () => {
  let component: ExerciseWriteTranslatedSentenceContainerComponent;
  let fixture: ComponentFixture<ExerciseWriteTranslatedSentenceContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseWriteTranslatedSentenceContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseWriteTranslatedSentenceContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

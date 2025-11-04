import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionOptionsReviewFlashcardComponent } from './session-options-review-flashcard.component';

describe('SessionOptionsReviewFlashcardComponent', () => {
  let component: SessionOptionsReviewFlashcardComponent;
  let fixture: ComponentFixture<SessionOptionsReviewFlashcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionOptionsReviewFlashcardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessionOptionsReviewFlashcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

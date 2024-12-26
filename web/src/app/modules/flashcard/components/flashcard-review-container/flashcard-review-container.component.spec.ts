import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardReviewContainerComponent } from './flashcard-review-container.component';

describe('FlashcardReviewContainerComponent', () => {
  let component: FlashcardReviewContainerComponent;
  let fixture: ComponentFixture<FlashcardReviewContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardReviewContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardReviewContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardViewContainerComponent } from './flashcard-view-container.component';

describe('FlashcardViewContainerComponent', () => {
  let component: FlashcardViewContainerComponent;
  let fixture: ComponentFixture<FlashcardViewContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardViewContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardViewContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

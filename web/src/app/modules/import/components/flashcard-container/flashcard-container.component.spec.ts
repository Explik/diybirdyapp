import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardContainerComponent } from './flashcard-container.component';

describe('FlashcardContainerComponent', () => {
  let component: FlashcardContainerComponent;
  let fixture: ComponentFixture<FlashcardContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardDeckViewPageComponent } from './flashcard-deck-view-page.component';

describe('FlashcardDeckViewPageComponent', () => {
  let component: FlashcardDeckViewPageComponent;
  let fixture: ComponentFixture<FlashcardDeckViewPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardDeckViewPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardDeckViewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

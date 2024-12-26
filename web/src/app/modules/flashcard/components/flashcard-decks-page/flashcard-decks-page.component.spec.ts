import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardDecksPageComponent } from './flashcard-decks-page.component';

describe('FlashcardDecksPageComponent', () => {
  let component: FlashcardDecksPageComponent;
  let fixture: ComponentFixture<FlashcardDecksPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardDecksPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardDecksPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

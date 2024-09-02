import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardDeckPageComponent } from './flashcard-deck-page.component';

describe('FlashcardDeckPageComponent', () => {
  let component: FlashcardDeckPageComponent;
  let fixture: ComponentFixture<FlashcardDeckPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardDeckPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardDeckPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

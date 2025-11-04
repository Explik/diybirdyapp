import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardDeckEditPageComponent } from './flashcard-deck-edit-page.component';

describe('FlashcardDeckEditPageComponent', () => {
  let component: FlashcardDeckEditPageComponent;
  let fixture: ComponentFixture<FlashcardDeckEditPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardDeckEditPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardDeckEditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

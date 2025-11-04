import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionOptionsSelectFlashcardComponent } from './session-options-select-flashcard.component';

describe('SessionOptionsSelectFlashcardComponent', () => {
  let component: SessionOptionsSelectFlashcardComponent;
  let fixture: ComponentFixture<SessionOptionsSelectFlashcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionOptionsSelectFlashcardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessionOptionsSelectFlashcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionOptionsLearnFlashcardComponent } from './session-options-learn-flashcard.component';

describe('SessionOptionsLearnFlashcardComponent', () => {
  let component: SessionOptionsLearnFlashcardComponent;
  let fixture: ComponentFixture<SessionOptionsLearnFlashcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionOptionsLearnFlashcardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessionOptionsLearnFlashcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

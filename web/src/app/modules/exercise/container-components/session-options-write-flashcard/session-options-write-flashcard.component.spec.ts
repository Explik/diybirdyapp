import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionOptionsWriteFlashcardComponent } from './session-options-write-flashcard.component';

describe('SessionOptionsWriteFlashcardComponent', () => {
  let component: SessionOptionsWriteFlashcardComponent;
  let fixture: ComponentFixture<SessionOptionsWriteFlashcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionOptionsWriteFlashcardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessionOptionsWriteFlashcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationFlashcardComponent } from './translation-flashcard.component';

describe('TranslationFlashcardComponent', () => {
  let component: TranslationFlashcardComponent;
  let fixture: ComponentFixture<TranslationFlashcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TranslationFlashcardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TranslationFlashcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

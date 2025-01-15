import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicFlashcardContentComponent } from './dynamic-flashcard-content.component';

describe('DynamicFlashcardContentComponent', () => {
  let component: DynamicFlashcardContentComponent;
  let fixture: ComponentFixture<DynamicFlashcardContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DynamicFlashcardContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicFlashcardContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

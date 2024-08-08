import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericFlashcardComponent } from './generic-flashcard.component';

describe('GenericFlashcardComponent', () => {
  let component: GenericFlashcardComponent;
  let fixture: ComponentFixture<GenericFlashcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenericFlashcardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenericFlashcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

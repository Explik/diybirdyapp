import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardEditContainerComponent } from './flashcard-edit-container.component';

describe('FlashcardEditContainerComponent', () => {
  let component: FlashcardEditContainerComponent;
  let fixture: ComponentFixture<FlashcardEditContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardEditContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardEditContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CorrectableMultipleChoiceFieldComponent } from './correctable-multiple-choice-field.component';

describe('CorrectableMultipleChoiceFieldComponent', () => {
  let component: CorrectableMultipleChoiceFieldComponent;
  let fixture: ComponentFixture<CorrectableMultipleChoiceFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CorrectableMultipleChoiceFieldComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CorrectableMultipleChoiceFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

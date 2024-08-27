import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CorrectableMultipleChoiceTextFieldComponent } from './correctable-multiple-choice-text-field.component';

describe('CorrectableMultipleChoiceTextFieldComponent', () => {
  let component: CorrectableMultipleChoiceTextFieldComponent;
  let fixture: ComponentFixture<CorrectableMultipleChoiceTextFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CorrectableMultipleChoiceTextFieldComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CorrectableMultipleChoiceTextFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

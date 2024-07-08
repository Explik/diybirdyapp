import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CorrectableTextFieldComponent } from './correctable-text-field.component';

describe('CorrectableTextFieldComponent', () => {
  let component: CorrectableTextFieldComponent;
  let fixture: ComponentFixture<CorrectableTextFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CorrectableTextFieldComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CorrectableTextFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

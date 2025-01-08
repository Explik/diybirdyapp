import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CorrectableAudioFieldComponent } from './correctable-audio-field.component';

describe('CorrectableAudioFieldComponent', () => {
  let component: CorrectableAudioFieldComponent;
  let fixture: ComponentFixture<CorrectableAudioFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CorrectableAudioFieldComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CorrectableAudioFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

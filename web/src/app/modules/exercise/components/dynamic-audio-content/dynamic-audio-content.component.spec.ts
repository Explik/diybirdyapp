import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicAudioContentComponent } from './dynamic-audio-content.component';

describe('DynamicAudioContentComponent', () => {
  let component: DynamicAudioContentComponent;
  let fixture: ComponentFixture<DynamicAudioContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DynamicAudioContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicAudioContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

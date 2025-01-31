import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicTextContentComponent } from './dynamic-text-content.component';

describe('DynamicTextContentComponent', () => {
  let component: DynamicTextContentComponent;
  let fixture: ComponentFixture<DynamicTextContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DynamicTextContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicTextContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

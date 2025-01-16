import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicVideoContentComponent } from './dynamic-video-content.component';

describe('DynamicVideoContentComponent', () => {
  let component: DynamicVideoContentComponent;
  let fixture: ComponentFixture<DynamicVideoContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DynamicVideoContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicVideoContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

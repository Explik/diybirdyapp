import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicImageContentComponent } from './dynamic-image-content.component';

describe('DynamicImageContentComponent', () => {
  let component: DynamicImageContentComponent;
  let fixture: ComponentFixture<DynamicImageContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DynamicImageContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicImageContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

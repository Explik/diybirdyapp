import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExitIconButtonComponent } from './exit-icon-button.component';

describe('ExitIconButtonComponent', () => {
  let component: ExitIconButtonComponent;
  let fixture: ComponentFixture<ExitIconButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExitIconButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExitIconButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

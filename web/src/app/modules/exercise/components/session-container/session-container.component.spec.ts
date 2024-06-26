import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionContainerComponent } from './session-container.component';

describe('SessionContainerComponent', () => {
  let component: SessionContainerComponent;
  let fixture: ComponentFixture<SessionContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessionContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

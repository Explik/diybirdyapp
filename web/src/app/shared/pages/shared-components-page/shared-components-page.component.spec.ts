import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedComponentsPageComponent } from './shared-components-page.component';

describe('SharedComponentsPageComponent', () => {
  let component: SharedComponentsPageComponent;
  let fixture: ComponentFixture<SharedComponentsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedComponentsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SharedComponentsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

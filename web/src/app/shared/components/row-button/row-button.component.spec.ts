import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RowButtonComponent } from './row-button.component';

describe('RowButtonComponent', () => {
  let component: RowButtonComponent;
  let fixture: ComponentFixture<RowButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RowButtonComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RowButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('prevents default on primary mouse down to avoid click focus', () => {
    const buttonEl = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    const event = new MouseEvent('mousedown', { button: 0, bubbles: true, cancelable: true });
    const preventDefaultSpy = spyOn(event, 'preventDefault').and.callThrough();

    buttonEl.dispatchEvent(event);

    expect(preventDefaultSpy).toHaveBeenCalled();
  });
});
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonComponent } from './button.component';

describe('ButtonComponent', () => {
  let component: ButtonComponent;
  let fixture: ComponentFixture<ButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('prevents default on primary mouse down to avoid click focus', () => {
    const buttonEl = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    const event = new MouseEvent('mousedown', { button: 0, bubbles: true, cancelable: true });
    const preventDefaultSpy = spyOn(event, 'preventDefault').and.callThrough();

    buttonEl.dispatchEvent(event);

    expect(preventDefaultSpy).toHaveBeenCalled();
  });
});

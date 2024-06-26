import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuoteComponent } from './text-quote.component';

describe('TextQuoteComponent', () => {
  let component: TextQuoteComponent;
  let fixture: ComponentFixture<TextQuoteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TextQuoteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TextQuoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

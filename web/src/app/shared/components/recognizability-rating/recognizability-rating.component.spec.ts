import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecognizabilityRatingComponent } from './recognizability-rating.component';

describe('RecognizabilityRatingComponent', () => {
  let component: RecognizabilityRatingComponent;
  let fixture: ComponentFixture<RecognizabilityRatingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecognizabilityRatingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecognizabilityRatingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

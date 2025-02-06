import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseComponentsPageComponent } from './exercise-components-page.component';

describe('ExerciseComponentsPageComponent', () => {
  let component: ExerciseComponentsPageComponent;
  let fixture: ComponentFixture<ExerciseComponentsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseComponentsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseComponentsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

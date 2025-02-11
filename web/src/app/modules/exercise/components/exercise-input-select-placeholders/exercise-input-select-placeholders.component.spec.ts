import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseInputSelectPlaceholdersComponent } from './exercise-input-select-placeholders.component';

describe('ExerciseInputSelectPlaceholdersComponent', () => {
  let component: ExerciseInputSelectPlaceholdersComponent;
  let fixture: ComponentFixture<ExerciseInputSelectPlaceholdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInputSelectPlaceholdersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInputSelectPlaceholdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

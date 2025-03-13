import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseInputWritePlaceholdersComponent } from './exercise-input-write-placeholders.component';

describe('ExerciseInputWritePlaceholdersComponent', () => {
  let component: ExerciseInputWritePlaceholdersComponent;
  let fixture: ComponentFixture<ExerciseInputWritePlaceholdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInputWritePlaceholdersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInputWritePlaceholdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

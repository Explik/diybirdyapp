import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExerciseInputSelectOptionsComponent } from './exercise-input-select-options.component';

describe('ExerciseInputSelectOptionsComponent', () => {
  let component: ExerciseInputSelectOptionsComponent;
  let fixture: ComponentFixture<ExerciseInputSelectOptionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInputSelectOptionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInputSelectOptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

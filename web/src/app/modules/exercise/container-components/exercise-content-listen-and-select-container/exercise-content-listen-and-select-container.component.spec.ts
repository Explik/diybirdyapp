import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseContentListenAndSelectContainerComponent } from './exercise-content-listen-and-select-container.component';

describe('ExerciseContentListenAndSelectContainerComponent', () => {
  let component: ExerciseContentListenAndSelectContainerComponent;
  let fixture: ComponentFixture<ExerciseContentListenAndSelectContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseContentListenAndSelectContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseContentListenAndSelectContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

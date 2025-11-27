import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseContentListenAndWriteContainerComponent } from './exercise-content-listen-and-write-container.component';

describe('ExerciseContentListenAndWriteContainerComponent', () => {
  let component: ExerciseContentListenAndWriteContainerComponent;
  let fixture: ComponentFixture<ExerciseContentListenAndWriteContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseContentListenAndWriteContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseContentListenAndWriteContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

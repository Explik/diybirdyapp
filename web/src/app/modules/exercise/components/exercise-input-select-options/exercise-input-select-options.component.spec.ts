import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SimpleChange } from '@angular/core';
import { ExerciseInputSelectOptionsComponent } from './exercise-input-select-options.component';
import { ExerciseInputSelectOptionsDto } from '../../../../shared/api-client';

function createInput(overrides: Partial<ExerciseInputSelectOptionsDto>): ExerciseInputSelectOptionsDto {
  return {
    id: 'input-1',
    sessionId: 'session-1',
    type: 'select-options',
    options: [],
    ...overrides,
  };
}

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

  it('keeps compact layout for audio options', () => {
    component.input = createInput({
      optionType: 'audio',
      options: [{ id: 'audio-1' }],
    });

    component.ngOnChanges({
      input: new SimpleChange(undefined, component.input, true),
    });

    expect(component.isVerticalOptionsLayout).toBeFalse();
    expect(component.optionHeightClass).toBe('h-14');
  });

  it('uses vertical layout for image options', () => {
    component.input = createInput({
      optionType: 'image',
      options: [{ id: 'image-1' }],
    });

    component.ngOnChanges({
      input: new SimpleChange(undefined, component.input, true),
    });

    expect(component.isVerticalOptionsLayout).toBeTrue();
    expect(component.optionHeightClass).toBe('h-28');
  });

  it('keeps compact layout for short text options', () => {
    component.input = createInput({
      optionType: 'text',
      options: [{ id: 'text-1', text: 'Short text option' } as any],
    });

    component.ngOnChanges({
      input: new SimpleChange(undefined, component.input, true),
    });

    expect(component.isVerticalOptionsLayout).toBeFalse();
  });

  it('uses vertical layout for long text options', () => {
    component.input = createInput({
      optionType: 'text',
      options: [{ id: 'text-1', text: 'x'.repeat(100) } as any],
    });

    component.ngOnChanges({
      input: new SimpleChange(undefined, component.input, true),
    });

    expect(component.isVerticalOptionsLayout).toBeTrue();
    expect(component.optionHeightClass).toBe('h-28');
  });
});

import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExerciseInputWriteTextDto } from '../../../../shared/api-client';
import { IconComponent } from "../../../../shared/components/icon/icon.component";
import { TextFieldComponent } from '../../../../shared/components/text-field/text-field.component';

@Component({
  selector: 'app-exercise-input-write-text',
  standalone: true,
  templateUrl: './exercise-input-write-text.component.html',
  imports: [CommonModule, FormsModule, IconComponent, TextFieldComponent]
})
export class ExerciseInputWriteTextComponent implements OnInit {
  @Input({required: true}) input!: ExerciseInputWriteTextDto;
  feedbackValues: { state: string, value: string}[] = []; 

  ngOnInit() {
    this.updateValues(this.input);
  }

  ngOnChanges() {
    this.updateValues(this.input);
  }

  updateValues(newValue: ExerciseInputWriteTextDto) {
    const feedbackValues: { state: string, value: string}[] = [];

    if (newValue?.feedback?.correctValues) 
      feedbackValues.push(...newValue.feedback.correctValues.map(value => ({ state: 'success', value })));
    
    if (newValue?.feedback?.incorrectValues) 
      feedbackValues.push(...newValue.feedback.incorrectValues.map(value => ({ state: 'failure', value })));

    this.feedbackValues = feedbackValues;
  }
}
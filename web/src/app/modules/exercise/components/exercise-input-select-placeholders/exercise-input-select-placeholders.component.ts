import { Component, ElementRef, ViewChild } from '@angular/core';
import { CdkDragDrop, DragDropModule, transferArrayItem, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';

interface Part {
  type: 'text' | 'placeholder';
  value?: string;
  option?: { id: string, text: string };
}

@Component({
  selector: 'app-exercise-input-select-placeholders',
  imports: [CommonModule, DragDropModule],
  templateUrl: './exercise-input-select-placeholders.component.html',
  styleUrl: './exercise-input-select-placeholders.component.css'
})
export class ExerciseInputSelectPlaceholdersComponent {
  @ViewChild('optionList') optionList!: ElementRef;

  parts: Part[] = [
    { type: 'text', value: 'This is a ' },
    { type: 'placeholder', option: undefined }
  ];

  options = [
    { id: '1', text: 'test' },
    { id: '2', text: 'example' }
  ];

  get placeholderSize(): number {
    const maxOptionSize = Math.max(...this.options.map(option => option.text.length));
    const maxPlaceholderSize = Math.max(...this.parts.filter(part => part.type === 'placeholder').map(part => part.option?.text.length ?? 0));
    return Math.max(maxOptionSize, maxPlaceholderSize) + 2; // Add some padding to compensate for the surrounding box
  }

  getOptionWithId(id: string) {
    return this.options.find(option => option.id === id);
  }

  drop(event: CdkDragDrop<any>, target: 'placeholders' | 'options') {
    // Move option within list
    if (event.previousContainer.element === event.container.element) {
      if (target === 'options') moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      else if (target === 'placeholders') return; // Do nothing
    }
    // Move option from placeholders to options
    else if (target === 'options') {
      const part = event.item.data;
      const option = part.option;

      part.option = undefined;
      this.options.splice(event.currentIndex, 0, option);
    }
    // Move option from options to placeholders
    else if (target === 'placeholders') {
      const part = event.container.data;
      const option = event.item.data;
      
      if (part.option)
        return; // Do nothing

      part.option = option;

      const optionIndex = this.options.indexOf(option);
      this.options.splice(optionIndex, 1);
    }
    else throw new Error('Unsupported drag-drop action');
  }
}

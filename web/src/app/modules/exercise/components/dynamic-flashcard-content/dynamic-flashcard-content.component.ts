import { Component, Input } from '@angular/core';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { CommonModule } from '@angular/common';
import { DynamicContentComponent } from '../dynamic-content/dynamic-content.component';

@Component({
  selector: 'app-dynamic-flashcard-content',
  standalone: true,
  imports: [CommonModule, FlashcardComponent, DynamicContentComponent],
  templateUrl: './dynamic-flashcard-content.component.html'
})
export class DynamicFlashcardContentComponent {
  @Input() data?: ExerciseContentFlashcardDto; 

  constructor() {}
}

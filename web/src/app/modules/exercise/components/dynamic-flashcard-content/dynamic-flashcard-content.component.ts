import { Component, Input } from '@angular/core';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { DynamicTextContentComponent } from "../dynamic-text-content/dynamic-text-content.component";
import { CommonModule } from '@angular/common';
import { DynamicImageContentComponent } from '../dynamic-image-content/dynamic-image-content.component';
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

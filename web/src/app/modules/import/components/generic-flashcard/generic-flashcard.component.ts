import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-generic-flashcard',
  standalone: true,
  imports: [],
  templateUrl: './generic-flashcard.component.html',
  styleUrl: './generic-flashcard.component.css'
})
export class GenericFlashcardComponent {
  @Output() switchSides = new EventEmitter<void>();

  handleSwitchSides() {
    this.switchSides?.emit();
  }
}

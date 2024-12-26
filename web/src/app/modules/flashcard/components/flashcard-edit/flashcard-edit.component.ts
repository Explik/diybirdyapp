import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-flashcard-edit',
  standalone: true,
  imports: [],
  templateUrl: './flashcard-edit.component.html',
  styleUrl: './flashcard-edit.component.css'
})
export class FlashcardEditComponent {
  @Output() switchSides = new EventEmitter<void>();

  handleSwitchSides() {
    this.switchSides?.emit();
  }
}

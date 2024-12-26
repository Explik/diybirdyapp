import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-recognizability-rating',
  standalone: true,
  imports: [],
  templateUrl: './recognizability-rating.component.html',
  styleUrl: './recognizability-rating.component.css'
})
export class RecognizabilityRatingComponent {
  // Output event to emit the selected rating
  @Output() ratingSelected: EventEmitter<string> = new EventEmitter<string>();

  // Function to handle button click and emit the selected rating
  selectRating(rating: string) {
    this.ratingSelected.emit(rating);
  }
}

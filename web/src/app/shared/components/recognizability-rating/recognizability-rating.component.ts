import { Component, EventEmitter, Output } from '@angular/core';
import { IconComponent } from '../icon/icon.component';
import { RowButtonComponent } from '../row-button/row-button.component';

@Component({
  selector: 'app-recognizability-rating',
  standalone: true,
  imports: [RowButtonComponent, IconComponent],
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

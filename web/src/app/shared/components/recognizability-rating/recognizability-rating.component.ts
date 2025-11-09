import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { IconComponent } from '../icon/icon.component';
import { RowButtonComponent } from '../row-button/row-button.component';
import { HotkeyService } from '../../services/hotKey.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-recognizability-rating',
  standalone: true,
  imports: [RowButtonComponent, IconComponent],
  templateUrl: './recognizability-rating.component.html',
  styleUrl: './recognizability-rating.component.css'
})
export class RecognizabilityRatingComponent implements OnInit, OnDestroy{
  private subs = new Subscription();
  
  @Output() ratingSelected: EventEmitter<string> = new EventEmitter<string>();

  constructor(private HotkeyService: HotkeyService) {}
  
  ngOnInit(): void {
    this.subs.add(
      this.HotkeyService.onHotkey({ key: '1' }).subscribe(() => this.selectRating('again'))
    );
    this.subs.add(
      this.HotkeyService.onHotkey({ key: '2' }).subscribe(() => this.selectRating('hard'))
    );
    this.subs.add(
      this.HotkeyService.onHotkey({ key: '3' }).subscribe(() => this.selectRating('good'))
    );
    this.subs.add(
      this.HotkeyService.onHotkey({ key: '4' }).subscribe(() => this.selectRating('easy'))
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  // Function to handle button click and emit the selected rating
  selectRating(rating: string) {
    this.ratingSelected.emit(rating);
  }
}

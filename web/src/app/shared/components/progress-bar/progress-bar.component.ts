import { Component, Input, signal } from '@angular/core';


@Component({
  selector: 'app-progress-bar',
  standalone: true,
  imports: [],
  templateUrl: './progress-bar.component.html',
  styleUrl: './progress-bar.component.css'
})
export class ProgressBarComponent {
    @Input() set progress(value: number) {
      this._progress.set(value);
    }
    
    get progress(): number {
      return this._progress();
    }

    private _progress = signal(0);
    
    getProgressBarColor() {
      const progress = this._progress();
      if (progress <= 25) return 'bg-red-600';
      if (progress <= 50) return 'bg-yellow-400';
      return 'bg-green-600';
    }
}

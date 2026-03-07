import { Component, Input, signal } from '@angular/core';


@Component({
  selector: 'app-batch-progress-bar',
  standalone: true,
  imports: [],
  templateUrl: './batch-progress-bar.component.html',
  styleUrl: './batch-progress-bar.component.css'
})
export class BatchProgressBarComponent {
    @Input() set progress(value: number) {
      this._progress.set(value);
    }
    
    @Input() set hasMoreBatches(value: boolean) {
      this._hasMoreBatches.set(value);
    }
    
    get progress(): number {
      return this._progress();
    }

    get hasMoreBatches(): boolean {
      return this._hasMoreBatches();
    }

    private _progress = signal(0);
    private _hasMoreBatches = signal(false);
    
    getProgressBarColor() {
      const progress = this._progress();
      if (progress <= 25) return 'bg-red-600';
      if (progress <= 50) return 'bg-yellow-400';
      return 'bg-green-600';
    }
}

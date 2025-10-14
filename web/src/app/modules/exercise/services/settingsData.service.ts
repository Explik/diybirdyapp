import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ExerciseSessionOptions } from '../models/exercise-session-options.interface';

@Injectable({
  providedIn: 'root'
})
export class SettingsDataService {
  private sessionType$ = new BehaviorSubject<string>('');
  private config$ = new BehaviorSubject<ExerciseSessionOptions | null>(null);
  private saveCallback: ((config: ExerciseSessionOptions) => void) | null = null;
  private cancelCallback: (() => void) | null = null;

  getSessionType(): Observable<string> {
    return this.sessionType$.asObservable();
  }

  getConfig(): Observable<ExerciseSessionOptions | null> {
    return this.config$.asObservable();
  }

  setData(sessionType: string, config: ExerciseSessionOptions) {
    this.sessionType$.next(sessionType);
    this.config$.next(config);
  }

  setCallbacks(saveCallback: (config: ExerciseSessionOptions) => void, cancelCallback: () => void) {
    this.saveCallback = saveCallback;
    this.cancelCallback = cancelCallback;
  }

  save(config: ExerciseSessionOptions) {
    if (this.saveCallback) {
      this.saveCallback(config);
    }
  }

  cancel() {
    if (this.cancelCallback) {
      this.cancelCallback();
    }
  }
}
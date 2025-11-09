import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { filter, fromEvent, Observable, Subject, Subscription } from 'rxjs';

/**
 * The global hotkey root service.
 * - Listens to all keydown events at the window level.
 * - Manages active hotkey scopes (boundaries).
 * - Used internally by scoped HotkeyService instances.
 */
@Injectable({ providedIn: 'root' })
export class HotkeyRootService implements OnDestroy {
  private keydown$ = new Subject<KeyboardEvent>();
  private subscription: Subscription;
  private activeScopes: string[] = [];

  constructor(zone: NgZone) {
    this.subscription = fromEvent<KeyboardEvent>(window, 'keydown')
      .pipe(filter(e => e instanceof KeyboardEvent)) // Prevents error "Uncaught Error: Attempting to use a disconnected port object..."
      .subscribe(event => zone.runOutsideAngular(() => this.keydown$.next(event)));
  }

  /** Observable of all keydown events */
  get events$(): Observable<KeyboardEvent> {
    return this.keydown$.asObservable();
  }

  /** Register a new active hotkey scope */
  activateScope(id: string): void {
    this.activeScopes.push(id);
  }

  /** Unregister a scope (called on destroy) */
  deactivateScope(id: string): void {
    this.activeScopes = this.activeScopes.filter(x => x !== id);
  }

  /** Returns the currently active (topmost) scope ID */
  get currentScope(): string | null {
    return this.activeScopes.length
      ? this.activeScopes[this.activeScopes.length - 1]
      : null;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.keydown$.complete();
  }
}

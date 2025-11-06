import { Injectable, OnDestroy, inject } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { HotkeyRootService } from './hotKeyRoot.service';

/** Describes a hotkey combination */
export interface HotkeyEvent {
  key: string;       // e.g. 's', 'enter', 'left', 'escape'
  ctrl?: boolean;    // Logical "Control" (Cmd on Mac, Ctrl on Windows/Linux)
  alt?: boolean;
  shift?: boolean;
  meta?: boolean;    // Explicit Command/Windows key if needed
}

/**
 * Scoped hotkey service.
 * - Automatically registers itself as an active boundary.
 * - Only receives events when this scope is active.
 * - Supports cross-platform modifiers (Ctrl vs Cmd).
 * - Supports letter and non-letter keys (Enter, Arrows, etc.).
 */
@Injectable()
export class HotkeyService implements OnDestroy {
  private root = inject(HotkeyRootService) as HotkeyRootService;
  private subs = new Subscription();
  private localEvents$ = new Subject<KeyboardEvent>();
  private isMac = navigator.platform.toLowerCase().includes('mac');
  private id = `scope-${Math.random().toString(36).slice(2, 9)}`;

  constructor() {
    // Register this scope as active
    this.root.activateScope(this.id);

    // Subscribe to global keydown events but only emit when active
    const sub = this.root.events$
      .pipe(filter(() => this.isActiveScope()))
      .subscribe(e => this.localEvents$.next(e));

    this.subs.add(sub);
  }

  /** Whether this service is currently the active hotkey scope */
  private isActiveScope(): boolean {
    return this.root.currentScope === this.id;
  }

  /** Normalize key values for consistent matching */
  private normalizeKey(key: string): string {
    const k = key.toLowerCase();
    switch (k) {
      case ' ': case 'spacebar': return 'space';
      case 'arrowleft': return 'left';
      case 'arrowright': return 'right';
      case 'arrowup': return 'up';
      case 'arrowdown': return 'down';
      case 'esc': return 'escape';
      default: return k;
    }
  }

  /** Match modifier keys cross-platform */
  private matchesModifiers(event: KeyboardEvent, hotkey: HotkeyEvent): boolean {
    const ctrlMatch = hotkey.ctrl
      ? this.isMac ? event.metaKey : event.ctrlKey
      : true;
    const altMatch = hotkey.alt ? event.altKey : true;
    const shiftMatch = hotkey.shift ? event.shiftKey : true;
    const metaMatch = hotkey.meta ? event.metaKey : true;
    return ctrlMatch && altMatch && shiftMatch && metaMatch;
  }

  /** Returns an observable that emits when the given hotkey is pressed */
  onHotkey(hotkey: HotkeyEvent) {
    const normalizedKey = this.normalizeKey(hotkey.key);

    return this.localEvents$.pipe(
      filter((event: KeyboardEvent) => {
        const eventKey = this.normalizeKey(event.key);
        const matchKey = eventKey === normalizedKey;
        return matchKey && this.matchesModifiers(event, hotkey);
      }),
      map(() => true)
    );
  }

  ngOnDestroy() {
    this.root.deactivateScope(this.id);
    this.subs.unsubscribe();
    this.localEvents$.complete();
  }
}

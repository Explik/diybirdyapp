import { TestBed } from '@angular/core/testing';
import { Subject } from 'rxjs';

import { HotkeyService } from './hotKey.service';
import { HotkeyRootService } from './hotKeyRoot.service';

const expectJasmine = expect as unknown as (actual: unknown) => any;

class MockHotkeyRootService {
  private eventsSubject = new Subject<KeyboardEvent>();
  private activeScopes: string[] = [];

  get events$() {
    return this.eventsSubject.asObservable();
  }

  get currentScope(): string | null {
    return this.activeScopes.length
      ? this.activeScopes[this.activeScopes.length - 1]
      : null;
  }

  activateScope(id: string): void {
    this.activeScopes.push(id);
  }

  deactivateScope(id: string): void {
    this.activeScopes = this.activeScopes.filter(scopeId => scopeId !== id);
  }

  emit(event: KeyboardEvent): void {
    this.eventsSubject.next(event);
  }
}

describe('HotkeyService', () => {
  let service: HotkeyService;
  let root: MockHotkeyRootService;
  let attachedElements: HTMLElement[];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        HotkeyService,
        { provide: HotkeyRootService, useClass: MockHotkeyRootService },
      ],
    });

    service = TestBed.inject(HotkeyService);
    root = TestBed.inject(HotkeyRootService) as unknown as MockHotkeyRootService;
    attachedElements = [];
  });

  afterEach(() => {
    service.ngOnDestroy();
    attachedElements.forEach((element) => element.remove());
    (document.activeElement as HTMLElement | null)?.blur();
  });

  function emitKey(key: string): void {
    root.emit(new KeyboardEvent('keydown', { key }));
  }

  function attachAndFocus(element: HTMLElement): void {
    document.body.appendChild(element);
    attachedElements.push(element);
    element.focus();
  }

  it('emits when key matches and no focus is blocking hotkeys', () => {
    let hitCount = 0;
    service.onHotkey({ key: '1' }).subscribe(() => hitCount++);

    emitKey('1');

    expectJasmine(hitCount).toBe(1);
  });

  it('does not emit when a text input is focused', () => {
    let hitCount = 0;
    const input = document.createElement('input');
    attachAndFocus(input);
    service.onHotkey({ key: '1' }).subscribe(() => hitCount++);

    emitKey('1');

    expectJasmine(hitCount).toBe(0);
  });

  it('does not emit when a focus-visible element is focused', () => {
    let hitCount = 0;
    const button = document.createElement('button');
    attachAndFocus(button);

    const originalMatches = button.matches.bind(button);
    spyOn(button, 'matches').and.callFake((selector: string) => {
      if (selector === ':focus-visible') {
        return true;
      }

      return originalMatches(selector);
    });

    service.onHotkey({ key: '1' }).subscribe(() => hitCount++);

    emitKey('1');

    expectJasmine(hitCount).toBe(0);
  });

  it('emits when focused element is not focus-visible', () => {
    let hitCount = 0;
    const button = document.createElement('button');
    attachAndFocus(button);

    const originalMatches = button.matches.bind(button);
    spyOn(button, 'matches').and.callFake((selector: string) => {
      if (selector === ':focus-visible') {
        return false;
      }

      return originalMatches(selector);
    });

    service.onHotkey({ key: '1' }).subscribe(() => hitCount++);

    emitKey('1');

    expectJasmine(hitCount).toBe(1);
  });
});

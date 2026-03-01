import { ErrorHandler, Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ErrorToast {
  id: number;
  message: string;
  stack?: string;
  expanded: boolean;
}

@Injectable({ providedIn: 'root' })
export class ErrorToastService implements ErrorHandler {
  private nextId = 0;
  private readonly toastsSubject = new BehaviorSubject<ErrorToast[]>([]);
  readonly toasts$ = this.toastsSubject.asObservable();

  handleError(error: unknown): void {
    // Log to console so existing tooling still works
    console.error(error);

    const message = this.extractMessage(error);
    const stack = this.extractStack(error);
    const toast: ErrorToast = { id: this.nextId++, message, stack, expanded: false };

    this.toastsSubject.next([...this.toastsSubject.value, toast]);

    // Auto-dismiss after 15 seconds
    setTimeout(() => this.dismiss(toast.id), 15_000);
  }

  dismiss(id: number): void {
    this.toastsSubject.next(this.toastsSubject.value.filter(t => t.id !== id));
  }

  toggleExpanded(id: number): void {
    this.toastsSubject.next(
      this.toastsSubject.value.map(t =>
        t.id === id ? { ...t, expanded: !t.expanded } : t
      )
    );
  }

  dismissAll(): void {
    this.toastsSubject.next([]);
  }

  private extractMessage(error: unknown): string {
    if (error instanceof Error) return error.message || 'An unexpected error occurred';
    if (typeof error === 'string') return error;
    try { return JSON.stringify(error); } catch { return 'An unexpected error occurred'; }
  }

  private extractStack(error: unknown): string | undefined {
    if (error instanceof Error) return error.stack;
    return undefined;
  }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ErrorToast, ErrorToastService } from '../../services/error-toast.service';

@Component({
  selector: 'app-error-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-toast.component.html',
  styleUrls: ['./error-toast.component.css']
})
export class ErrorToastComponent implements OnInit, OnDestroy {
  toasts: ErrorToast[] = [];
  private subscription?: Subscription;

  constructor(private errorToastService: ErrorToastService) {}

  ngOnInit(): void {
    this.subscription = this.errorToastService.toasts$.subscribe(toasts => {
      this.toasts = toasts;
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  dismiss(id: number): void {
    this.errorToastService.dismiss(id);
  }

  toggleExpanded(id: number): void {
    this.errorToastService.toggleExpanded(id);
  }

  dismissAll(): void {
    this.errorToastService.dismissAll();
  }

  trackById(_: number, toast: ErrorToast): number {
    return toast.id;
  }
}

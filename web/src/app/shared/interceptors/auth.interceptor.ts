import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Route, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private router: Router, private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    try {
      const apiBase = environment.apiUrl?.toString() ?? '';

      // If the request targets our API, ensure credentials (cookies) are sent.
      if (apiBase && req.url.startsWith(apiBase)) {
        const cloned = req.clone({ withCredentials: true });
        
        return next.handle(cloned).pipe(
          catchError((error: HttpErrorResponse) => {
            const isUnauthenticated = [401, 403].includes(error.status);
            if (isUnauthenticated) {
              this.authService.reauthenticate();
            }
            return throwError(() => error);
          })
        );
      }
    } catch (e) {
      // In case environment isn't available or other error, fallthrough to default handling
    }
    return next.handle(req);
  }
}

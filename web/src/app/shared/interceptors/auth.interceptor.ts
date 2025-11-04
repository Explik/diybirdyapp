import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    try {
      const apiBase = environment.apiUrl?.toString() ?? '';
      // If the request targets our API, ensure credentials (cookies) are sent.
      if (apiBase && req.url.startsWith(apiBase)) {
        const cloned = req.clone({ withCredentials: true });
        return next.handle(cloned);
      }
    } catch (e) {
      // In case environment isn't available or other error, fallthrough to default handling
    }
    return next.handle(req);
  }
}

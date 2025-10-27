import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private baseUrl = environment.apiUrl;
    constructor(private http: HttpClient) {}

    login(username: string, password: string): Observable<any> {
        const payload = { email: username, password };

        // Some backends return plain text (e.g. "User signed-in successfully!") which
        // causes HttpClient's default JSON parser to throw even on HTTP 200. Request
        // the response as text and try to parse JSON if possible.
        return this.http.post<any>(`${this.baseUrl}/auth/login`, payload, { responseType: 'text' as 'json' }).pipe(
            tap((res: any) => {
                // If server returned JSON (parsed), it may contain a token.
                if (res && typeof res === 'object' && res.token) {
                    localStorage.setItem('auth_token', res.token);
                    return;
                }

                // If server returned a plain text string, attempt to parse it as JSON
                // in case it serialized an object as a string.
                try {
                    const parsed = JSON.parse(res as unknown as string);
                    if (parsed && parsed.token) {
                        localStorage.setItem('auth_token', parsed.token);
                    }
                } catch (e) {
                    // ignore - response is plain text without token
                }
            })
        );
    }

    logout(): Observable<any> {
        // call logout endpoint and clear local token on success
        return this.http.post<any>(`${this.baseUrl}/auth/logout`, {}, { responseType: 'text' as 'json' }).pipe(
            tap(() => localStorage.removeItem('auth_token'))
        );
    }

    signup(username: string, password: string, name?: string): Observable<any> {
        const payload: any = { email: username, password };
        if (name) payload.name = name;
        return this.http.post<any>(`${this.baseUrl}/auth/signup`, payload, { responseType: 'text' as 'json' }).pipe(
            tap((res: any) => {
                // Accept token on signup if provided
                if (res && typeof res === 'object' && res.token) {
                    localStorage.setItem('auth_token', res.token);
                    return;
                }
                try {
                    const parsed = JSON.parse(res as unknown as string);
                    if (parsed && parsed.token) {
                        localStorage.setItem('auth_token', parsed.token);
                    }
                } catch (e) {
                    // ignore plain text response
                }
            })
        );
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('auth_token');
    }
}
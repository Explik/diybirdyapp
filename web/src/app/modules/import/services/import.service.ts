import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { TranslationFlashcard } from "../models/flashcard.model";

@Injectable({
    providedIn: 'root'
  })
  export class ImportService {
    private baseUrl = `${environment.apiUrl}/flashcard`;
  
    constructor(private http: HttpClient) { }
  
    createFlashcard<T>(exercise: T): Observable<any> {
      return this.http.post(this.baseUrl, exercise);
    }

    getFlashcards(): Observable<TranslationFlashcard[]> {
        return this.http.get<TranslationFlashcard[]>(this.baseUrl);
    }
  }
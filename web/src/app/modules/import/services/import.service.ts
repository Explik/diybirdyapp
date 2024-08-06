import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { TranslationFlashcard } from "../models/flashcard.model";

@Injectable({
    providedIn: 'root'
  })
  export class ImportService {
    private baseUrl = `${environment.apiUrl}/flashcard`;
  
    constructor(private http: HttpClient) { }


    createFlashcard(flashcard: any): Observable<TranslationFlashcard> {
        return this.http.post<TranslationFlashcard>(this.baseUrl, flashcard);
    }

    getFlashcards(): Observable<TranslationFlashcard[]> {
      return this.http.get<FlashcardDto[]>(this.baseUrl)
        .pipe(map((arr) => arr.map(x => ({
          leftLabel: x.leftLanguage.abbreviation,
          leftValue: x.leftValue,
          rightLabel: x.rightLanguage.abbreviation,
          rightValue: x.rightValue
        }) as TranslationFlashcard)));
    }
  }
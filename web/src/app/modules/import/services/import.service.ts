import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { TranslationFlashcard } from "../models/flashcard.model";
import { RecursivePartial } from "../../../shared/models/util.model";

@Injectable({
    providedIn: 'root'
  })
  export class ImportService {
    private baseUrl = `${environment.apiUrl}/flashcard`;
  
    constructor(private http: HttpClient) { }

    createFlashcard(flashcard: RecursivePartial<FlashcardDto>): Observable<TranslationFlashcard> {
        return this.http.post<FlashcardDto>(this.baseUrl, flashcard).pipe(map(this.mapDtoToModel));;
    }

    updateFlashcard(flashcard: RecursivePartial<FlashcardDto>): Observable<TranslationFlashcard> {
      return this.http
        .put<FlashcardDto>(this.baseUrl, flashcard)
        .pipe(map(this.mapDtoToModel));
    }

    getFlashcards(): Observable<TranslationFlashcard[]> {
      return this.http.get<FlashcardDto[]>(this.baseUrl)
        .pipe(map((arr) => arr.map(this.mapDtoToModel)));
    }

    mapDtoToModel(x: FlashcardDto): TranslationFlashcard {
      return {
        id: x.id,
        leftLabel: x.leftLanguage.abbreviation,
        leftValue: x.leftValue,
        rightLabel: x.rightLanguage.abbreviation,
        rightValue: x.rightValue
      };
    }
  }
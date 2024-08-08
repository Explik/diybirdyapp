import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { FlashcardLanguage, Flashcard } from "../models/flashcard.model";
import { RecursivePartial } from "../../../shared/models/util.model";

@Injectable({
    providedIn: 'root'
  })
  export class ImportService {
    private flashcardBaseUrl = `${environment.apiUrl}/flashcard`;
    private languageBaseUrl = `${environment.apiUrl}/language`;
  
    constructor(private http: HttpClient) { }

    createFlashcard(flashcard: RecursivePartial<FlashcardDto>): Observable<Flashcard> {
        return this.http.post<FlashcardDto>(this.flashcardBaseUrl, flashcard).pipe(map(this.mapDtoToModel));;
    }

    updateFlashcard(flashcard: RecursivePartial<FlashcardDto>): Observable<Flashcard> {
      return this.http
        .put<FlashcardDto>(this.flashcardBaseUrl, flashcard)
        .pipe(map(this.mapDtoToModel));
    }

    getFlashcards(): Observable<Flashcard[]> {
      return this.http.get<FlashcardDto[]>(this.flashcardBaseUrl)
        .pipe(map((arr) => arr.map(this.mapDtoToModel)));
    }

    getFlashcardLanguages(): Observable<FlashcardLanguage[]> {
      return this.http.get<LanguageDto[]>(this.languageBaseUrl)
        .pipe(map((arr) => arr.map(this.mapLanguageDtoToModel)));
    }
 
    mapDtoToModel(x: FlashcardDto): Flashcard {
      return {
        id: x.id,
        leftLanguage: x.leftLanguage,
        leftValue: x.leftValue,
        rightLanguage: x.rightLanguage,
        rightValue: x.rightValue
      };
    }

    mapLanguageDtoToModel(x: LanguageDto): LanguageModel {
      return {
        id: x.id,
        abbreviation: x.abbreviation,
        name: x.name
      };
    }
  }
import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { FlashcardLanguage, Flashcard, FlashcardDeck } from "../models/flashcard.model";
import { RecursivePartial } from "../../../shared/models/util.model";

@Injectable({
    providedIn: 'root'
  })
  export class ImportService {
    private exerciseSessionBaseUrl = `${environment.apiUrl}/exercise-session`;
    private flashcardBaseUrl = `${environment.apiUrl}/flashcard`;
    private flashcardDeckBaseUrl = `${environment.apiUrl}/flashcard-deck`;
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

    getFlashcards(deckId: string|null): Observable<Flashcard[]> {
      return this.http.get<FlashcardDto[]>(this.flashcardBaseUrl + "?deckId=" + deckId)
        .pipe(map((arr) => arr.map(this.mapDtoToModel)));
    }

    getFlashcardLanguages(): Observable<FlashcardLanguage[]> {
      return this.http.get<FlashcardLanguageDto[]>(this.languageBaseUrl)
        .pipe(map((arr) => arr.map(this.mapLanguageDtoToModel)));
    }

    createFlashcardDeck(flashcardDeck: RecursivePartial<FlashcardDeckDto>): Observable<FlashcardDeckDto> {
      return this.http.post<FlashcardDeckDto>(this.flashcardDeckBaseUrl, flashcardDeck).pipe(map(this.mapSetDtoToModel));
    }

    getFlashcardDeck(id: string): Observable<FlashcardDeck> {
      return this.http.get<FlashcardDeckDto>(this.flashcardDeckBaseUrl + "/" + id)
        .pipe(map(this.mapSetDtoToModel));
    }

    getFlashcardDecks(): Observable<FlashcardDeck[]> {
      return this.http.get<FlashcardDeckDto[]>(this.flashcardDeckBaseUrl)
        .pipe(map((arr) => arr.map(this.mapSetDtoToModel)));
    }

    updateFlashcardDeck(flashcardDeck: RecursivePartial<FlashcardDeckDto>): Observable<FlashcardDeckDto> {
      return this.http.put<FlashcardDeckDto>(this.flashcardDeckBaseUrl, flashcardDeck).pipe(map(this.mapSetDtoToModel));
    }

    reviewFlashcardDeck(deckId: string): Observable<ExerciseSessionDto> {
      return this.http.post<ExerciseSessionDto>(this.exerciseSessionBaseUrl, { 
        type: "review-flashcard-session", 
        flashcardDeckId: deckId 
      });
    }

    mapDtoToModel(x: FlashcardDto): Flashcard {
      return {
        id: x.id,
        deckId: x.deckId,
        leftLanguage: x.leftLanguage,
        leftValue: x.leftValue,
        rightLanguage: x.rightLanguage,
        rightValue: x.rightValue
      };
    }

    mapLanguageDtoToModel(x: FlashcardLanguageDto): FlashcardLanguage {
      return {
        id: x.id,
        abbreviation: x.abbreviation,
        name: x.name
      };
    }

    mapSetDtoToModel(x: FlashcardDeckDto): FlashcardDeck {
      return {
        id: x.id,
        name: x.name
      };
    }
  }
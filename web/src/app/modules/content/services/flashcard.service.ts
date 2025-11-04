import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { RecursivePartial } from "../../../shared/models/util.model";
import { FlashcardContent } from "../../../shared/models/content.interface";
import { EditFlashcard, EditFlashcardDeck, EditFlashcardDeckImpl, EditFlashcardImpl, EditFlashcardLanguageImpl } from "../models/editFlashcard.model";
import { ExerciseSessionDto, FlashcardDeckDto, FlashcardDto, FlashcardLanguageDto } from "../../../shared/api-client";

@Injectable({
  providedIn: 'root'
})
export class FlashcardService {
  private exerciseSessionBaseUrl = `${environment.apiUrl}/exercise-session`;
  private flashcardBaseUrl = `${environment.apiUrl}/flashcard`;
  private flashcardDeckBaseUrl = `${environment.apiUrl}/flashcard-deck`;
  private languageBaseUrl = `${environment.apiUrl}/language`;

  constructor(private http: HttpClient) { }

  createFlashcard(flashcard: RecursivePartial<FlashcardDto>): Observable<EditFlashcardImpl> {
    return this.http.post<FlashcardDto>(this.flashcardBaseUrl, flashcard).pipe(map(EditFlashcardImpl.createFromDto));;
  }

  updateFlashcard(flashcard: EditFlashcard): Observable<EditFlashcardImpl|undefined> {
    let allChanges = flashcard.getAllChanges();

    if (allChanges?.state === 'deleted') {
      return this.http
        .delete<FlashcardDto>(this.flashcardBaseUrl + "/" + allChanges?.flashcard.id)
        .pipe(map(s => undefined)); 
    }
    else {
      // Create form data
      const formData: FormData = new FormData();
      formData.append('flashcard', new Blob([JSON.stringify(allChanges?.flashcard)], { type: 'application/json' }));

      for (let file of allChanges?.files ?? []) {
        formData.append('files', file, file.name);
      }

      if (allChanges?.state === 'added') {
        // Send request
        return this.http
          .post<FlashcardDto>(this.flashcardBaseUrl + "/rich", formData)
          .pipe(map(EditFlashcardImpl.createFromDto));
      }
      else {
        // Send request
        return this.http
          .put<FlashcardDto>(this.flashcardBaseUrl + "/rich", formData)
          .pipe(map(EditFlashcardImpl.createFromDto));
      }
    }
  }

  pronounceFlashcardContent(id: string, side: 'left' | 'right'): Observable<Blob> {
    return this.http.post(`${this.flashcardBaseUrl}/${id}/text-to-speech/${side}`, {}, { responseType: 'blob' });
  }

  getFlashcards(deckId: string | null): Observable<EditFlashcardImpl[]> {
    return this.http.get<FlashcardDto[]>(this.flashcardBaseUrl + "?deckId=" + deckId)
      .pipe(map((arr) => arr.map(EditFlashcardImpl.createFromDto)));
  }

  getFlashcardLanguages(): Observable<EditFlashcardLanguageImpl[]> {
    return this.http.get<FlashcardLanguageDto[]>(this.languageBaseUrl)
      .pipe(map((arr) => arr.map(EditFlashcardLanguageImpl.createFromDto)));
  }

  createFlashcardDeck(flashcardDeck: RecursivePartial<FlashcardDeckDto>): Observable<EditFlashcardDeck> {
    return this.http.post<FlashcardDeckDto>(this.flashcardDeckBaseUrl, flashcardDeck).pipe(map(EditFlashcardDeckImpl.createFromDto));
  }

  getFlashcardDeck(id: string): Observable<EditFlashcardDeckImpl> {
    return this.http.get<FlashcardDeckDto>(this.flashcardDeckBaseUrl + "/" + id)
      .pipe(map(EditFlashcardDeckImpl.createFromDto));
  }

  getFlashcardDecks(): Observable<EditFlashcardDeckImpl[]> {
    return this.http.get<FlashcardDeckDto[]>(this.flashcardDeckBaseUrl)
      .pipe(map((arr) => arr.map(EditFlashcardDeckImpl.createFromDto)));
  }

  updateFlashcardDeck(flashcardDeck: RecursivePartial<FlashcardDeckDto>): Observable<FlashcardDeckDto> {
    return this.http.put<FlashcardDeckDto>(this.flashcardDeckBaseUrl, flashcardDeck).pipe(map(EditFlashcardDeckImpl.createFromDto));
  }

  selectFlashcardDeck(deckId: string): Observable<ExerciseSessionDto> {
    return this.http.post<ExerciseSessionDto>(this.exerciseSessionBaseUrl, {
      type: "select-flashcard-session",
      flashcardDeckId: deckId
    });
  }

  reviewFlashcardDeck(deckId: string): Observable<ExerciseSessionDto> {
    return this.http.post<ExerciseSessionDto>(this.exerciseSessionBaseUrl, {
      type: "review-flashcard-session",
      flashcardDeckId: deckId
    });
  }

  writeFlashcardDeck(deckId: string): Observable<ExerciseSessionDto> {
    return this.http.post<ExerciseSessionDto>(this.exerciseSessionBaseUrl, {
      type: "write-flashcard-session",
      flashcardDeckId: deckId
    });
  }

  learnFlashcardDeck(deckId: string): Observable<ExerciseSessionDto> {
    return this.http.post<ExerciseSessionDto>(this.exerciseSessionBaseUrl, {
      type: "learn-flashcard-session",
      flashcardDeckId: deckId
    });
  }
}
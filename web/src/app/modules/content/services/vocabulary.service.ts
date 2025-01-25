import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class VocabularyService {
    private vocabularyBaseUrl = `${environment.apiUrl}/vocabulary`; 

    constructor(private http: HttpClient) {}

    getVocabulary(): Observable<VocabularyDto> {
        return this.http.get<VocabularyDto>(this.vocabularyBaseUrl);
    }
}


import { Injectable, Type } from "@angular/core";
import { map, Observable } from "rxjs";
import { SessionOptionsSelectFlashcardComponent } from "../container-components/session-options-select-flashcard/session-options-select-flashcard.component";
import { ExerciseSessionDataService } from "./exerciseSessionData.service";
import { ExerciseService } from "./exercise.service";
import { SessionOptionsReviewFlashcardComponent } from "../container-components/session-options-review-flashcard/session-options-review-flashcard.component";
import { SessionOptionsWriteFlashcardComponent } from "../container-components/session-options-write-flashcard/session-options-write-flashcard.component";
import { SessionOptionsLearnFlashcardComponent } from "../container-components/session-options-learn-flashcard/session-options-learn-flashcard.component";

@Injectable({
    providedIn: 'root'
})
export class SessionOptionsComponentService {
    constructor(private service: ExerciseService) {}

    getComponent(): Observable<Type<any> | null> {
        return this.service.getExerciseSessionOptions().pipe(
            map(options => this.mapComponent(options?.type))
        );  
    }

    mapComponent(optionType?: string): Type<any> | null {
        if (!optionType)
            return null; 

        switch(optionType) {
            case "select-flashcard-session": 
                return SessionOptionsSelectFlashcardComponent;
            case "review-flashcard-session":
                return SessionOptionsReviewFlashcardComponent;
            case "write-flashcard-session":
                return SessionOptionsWriteFlashcardComponent;
            case "learn-flashcard-session":
                return SessionOptionsLearnFlashcardComponent;
            default: 
                throw new Error("Unknown session option type " + optionType);
        }
    }  
}
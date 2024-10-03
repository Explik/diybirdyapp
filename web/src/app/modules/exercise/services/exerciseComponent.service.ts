import { Injectable, Type } from "@angular/core";
import { ExerciseWriteSentenceUsingWordContainerComponent } from "../components/exercise-write-sentence-using-word-container/exercise-write-sentence-using-word-container.component";
import { ExerciseWriteTranslatedSentenceContainerComponent } from "../components/exercise-write-translated-sentence-container/exercise-write-translated-sentence-container.component";
import { ExerciseMultipleTextChoiceContainerComponent } from "../components/exercise-multiple-text-choice-container/exercise-multiple-text-choice-container.component";
import { ExerciseReviewFlashcardContentContainerComponent } from "../components/exercise-review-flashcard-content-container/exercise-review-flashcard-content-container.component";

@Injectable({
    providedIn: 'root'
})
export class ExerciseComponentService {
    getComponent(exerciseType: string): Type<any> {
        switch(exerciseType) {
            case "write-sentence-using-word-exercise": 
                return ExerciseWriteSentenceUsingWordContainerComponent;
            case "write-translated-sentence-exercise": 
                return ExerciseWriteTranslatedSentenceContainerComponent;
            case "multiple-choice-text-exercise":
                return ExerciseMultipleTextChoiceContainerComponent;
            case "review-flashcard-exercise":
                return ExerciseReviewFlashcardContentContainerComponent;
            default: 
                throw new Error("Unknown exercise type " + exerciseType);
        }
    }
}
import { Injectable, Type } from "@angular/core";
import { ExerciseContentWriteSentenceUsingWordContainerComponent } from "../components/exercise-content-write-sentence-using-word-container/exercise-content-write-sentence-using-word-container.component";
import { ExerciseContentWriteTranslatedSentenceContainerComponent } from "../components/exercise-content-write-translated-sentence-container/exercise-content-write-translated-sentence-container.component";
import { ExerciseContentMultipleTextChoiceContainerComponent } from "../components/exercise-content-multiple-text-choice-container/exercise-content-multiple-text-choice-container.component";
import { ExerciseContentReviewFlashcardContainerComponent } from "../components/exercise-content-review-flashcard-container/exercise-content-review-flashcard-container.component";
import { ExerciseNavigationCheckAnswerContainerComponent } from "../components/exercise-navigation-check-answer-container/exercise-navigation-check-answer-container.component";
import { ExerciseContentWriteFlashcardContentContainerComponent } from "../components/exercise-content-write-flashcard-content-container/exercise-content-write-flashcard-content-container.component";
import { map, Observable } from "rxjs";
import { ExerciseService } from "./exercise.service";
import { ExerciseContentLoadingComponent } from "../components/exercise-content-loading/exercise-content-loading.component";
import { ExerciseContentSelectFlashcardContainerComponent } from "../components/exercise-content-select-flashcard-container/exercise-content-select-flashcard-container.component";
import { ExerciseNavigationSkipOnlyContainerComponent } from "../components/exercise-navigation-only-answer-container/exercise-navigation-skip-only-container.component";

@Injectable({
    providedIn: 'root'
})
export class ExerciseComponentService {
    constructor(private exerciseService: ExerciseService) {}

    getComponent(): Observable<Type<any>> {
        return this.exerciseService.getExercise().pipe(
            map(exercise => this.mapComponent(exercise?.type))
        );
    }

    mapComponent(exerciseType?: string): Type<any> {
        if (exerciseType === undefined)
            return ExerciseContentLoadingComponent; 

        switch(exerciseType) {
            case "write-sentence-using-word-exercise": 
                return ExerciseContentWriteSentenceUsingWordContainerComponent;
            case "write-translated-sentence-exercise": 
                return ExerciseContentWriteTranslatedSentenceContainerComponent;
            case "write-flashcard-exercise":
                return ExerciseContentWriteFlashcardContentContainerComponent;
            case "multiple-choice-text-exercise":
                return ExerciseContentMultipleTextChoiceContainerComponent;
            case "select-flashcard-exercise":
                return ExerciseContentSelectFlashcardContainerComponent;
            case "review-flashcard-exercise":
                return ExerciseContentReviewFlashcardContainerComponent;
            default: 
                throw new Error("Unknown exercise type " + exerciseType);
        }
    }

    getNavigationComponent(): Observable<Type<any>|null> {
        return this.exerciseService.getExercise().pipe(
            map(exercise => this.mapNavigationComponent(exercise?.type))
        );
    }

    mapNavigationComponent(exerciseType?: string): Type<any>|null {
        switch(exerciseType) {
            case "write-sentence-using-word-exercise":
            case "write-translated-sentence-exercise":
            case "write-flashcard-exercise":
                return ExerciseNavigationCheckAnswerContainerComponent; 
            case "select-flashcard-exercise":
                return ExerciseNavigationSkipOnlyContainerComponent;
            default: 
                return null; 
        }
    }
}
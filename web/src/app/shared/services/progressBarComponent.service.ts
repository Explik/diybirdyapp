import { Injectable, Type } from "@angular/core";
import { Observable, map } from "rxjs";
import { ExerciseProgressBarComponent } from "../../modules/exercise/components/exercise-progress-bar/exercise-progress-bar.component";
import { ExerciseBatchProgressBarComponent } from "../../modules/exercise/components/exercise-batch-progress-bar/exercise-batch-progress-bar.component";
import { ExerciseSessionProgressDto } from "../api-client";

@Injectable({
    providedIn: 'root'
})
export class ProgressBarComponentService {
    getComponentForProgress(progress$: Observable<ExerciseSessionProgressDto | undefined>): Observable<Type<any>> {
        return progress$.pipe(
            map(progress => this.mapComponent(progress?.type))
        );
    }

    mapComponent(progressType?: string): Type<any> {
        if (!progressType) {
            // Default to regular progress bar
            return ExerciseProgressBarComponent;
        }

        switch(progressType) {
            case "percentage":
                return ExerciseProgressBarComponent;
            case "batch-percentage":
                return ExerciseBatchProgressBarComponent;
            default:
                return ExerciseProgressBarComponent;
        }
    }
}

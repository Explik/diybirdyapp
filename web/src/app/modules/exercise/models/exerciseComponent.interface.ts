import { Type } from "@angular/core";

export interface ExerciseComponent {
    component: Type<any>,
    inputs: Record<string, unknown>
}
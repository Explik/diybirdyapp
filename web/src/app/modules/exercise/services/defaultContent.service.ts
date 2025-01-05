import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";

/**
 * Service to provide default content for exercises as most exercises have fixed content
 */
@Injectable({
    providedIn: 'root'
})
export class DefaultContentService {
    constructor() {}

    getTextInput(): Partial<ExerciseInputTextDto> {
        const textInput: Partial<ExerciseInputTextDto> = {
            id: "default",
            type: "text",
            text: "",
            feedback: undefined,
        }
        return textInput;
    }
}

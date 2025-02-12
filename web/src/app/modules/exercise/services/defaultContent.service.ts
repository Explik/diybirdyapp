import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { ExerciseInputAudioDto, ExerciseInputTextDto } from "../../../shared/api-client";

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

    getAudioInput(): Partial<ExerciseInputAudioDto> {
        const audioInput: Partial<ExerciseInputAudioDto> = {
            id: "default",
            type: "audio",
            url: ""
        }
        return audioInput;
    }
}

import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { ExerciseInputRecordAudioDto, ExerciseInputWriteTextDto } from "../../../shared/api-client";

/**
 * Service to provide default content for exercises as most exercises have fixed content
 */
@Injectable({
    providedIn: 'root'
})
export class DefaultContentService {
    constructor() {}

    getTextInput(): Partial<ExerciseInputWriteTextDto> {
        const textInput: Partial<ExerciseInputWriteTextDto> = {
            id: "default",
            type: "text",
            text: "",
            feedback: undefined,
        }
        return textInput;
    }

    getAudioInput(): Partial<ExerciseInputRecordAudioDto> {
        const audioInput: Partial<ExerciseInputRecordAudioDto> = {
            id: "default",
            type: "audio",
            url: ""
        }
        return audioInput;
    }
}

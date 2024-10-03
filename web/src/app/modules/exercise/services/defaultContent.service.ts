import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { TextInput } from "../../../shared/models/input.interface";

/**
 * Service to provide default content for exercises as most exercises have fixed content
 */
@Injectable({
    providedIn: 'root'
})
export class DefaultContentService {
    constructor() {}

    getTextInput(): TextInput {
        const textInput: TextInput = {
            text: ""
        }
        return textInput;
    }
}

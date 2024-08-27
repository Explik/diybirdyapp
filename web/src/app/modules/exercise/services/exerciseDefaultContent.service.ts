import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { TextInput } from "../../../shared/models/input.interface";

@Injectable({
    providedIn: 'root'
})
export class ExerciseDefaultContentService {
    constructor() {}

    // For most exercises the input is fixed, so these are hardcoded in the UI
    getInput<T>(identifier: string): Observable<T> {
        if (identifier === "text-input") {
            const textInput: TextInput = {
                text: ""
            }
            return of(textInput as T);
        }
        throw new Error("Unsupported content " + identifier);
    }
}

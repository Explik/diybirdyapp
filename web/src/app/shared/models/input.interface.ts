type resultState = "success" | "failure" | "indecisive";

export interface TextInput {
    text: string
}

export interface CorrectableTextInput {
    text: string,
    feedback?: {
        state: resultState
    }
}

export interface CorrectableMultipleChoiceTextInput {
    options: Record<string, string>;
    feedback?: {
        state: resultState,
        correctOptionIds: string
    }
}
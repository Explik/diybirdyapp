type resultState = "success" | "failure" | "indecisive";

export interface TextInput {
    type: string,
    text: string
}

export interface TextInputFeedback {
    type: string,
    state: resultState,
    message: string|undefined
}

export interface MultipleChoiceTextInput {
    options: { id: string, text: string, result?: string }[]
}

export class DefaultInput {
    static TEXT_INPUT: TextInput = {
        type: "text",
        text: ""
    }
}
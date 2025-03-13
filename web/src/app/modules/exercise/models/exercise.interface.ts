export interface GenericExercise {
    id: string,
    type: string,
    data: any,
    header?: ExerciseHeader,
    subHeader?: ExerciseSubHeader,
    content?: GenericExerciseContent,
    input?: GenericExerciseInput,
    button?: GenericExerciseButton
}

export interface ExerciseHeader {
    text: string
}

export interface ExerciseSubHeader {
    text: string
}

export interface GenericExerciseContent {
    type: string
}

export interface TextExerciseContent extends GenericExerciseContent {
    text: string
}

export interface GenericExerciseInput {
    type: string
}

export interface TextExerciseInput extends GenericExerciseInput {
    type: "text-input"
}

export interface MultipleChoiceExerciseInput extends GenericExerciseInput {
    type: "multiple-choice-input",
    options: MultipleChoiceOption[];
}

export interface MultipleChoiceOption {
    id: string,
    text: string;
    result?: "success" | "failure";
}

export interface GenericExerciseButton {
    type: string
}

export interface CheckAnswerExerciseButton {
    type: "check-answer-button"
}

export interface Exercise {
    
}

export interface ExerciseAnswer {
    //id: string;
    //exerciseId: string;
    [key: string]: any;
    files?: File[];
}

export enum ExerciseStates {
    Unanswered, 
    Answered
}
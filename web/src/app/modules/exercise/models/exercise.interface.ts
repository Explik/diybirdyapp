export interface BaseExercise {
    id: string;
    exerciseType: string; 
    exerciseAnswer?: BaseExerciseAnswer
}

export interface WriteSentenceUsingWordExercise extends BaseExercise {
    word: string;
    exerciseAnswer?: WrittenExerciseAnswer
}

export interface TranslateSentenceExercise extends BaseExercise {
    originalSentence: string;
    targetLanguage: string;
    exerciseAnswer?: WrittenExerciseAnswer;
}

export interface BaseExerciseAnswer {
    id: string,
    answerType: string
}

export interface WrittenExerciseAnswer extends BaseExerciseAnswer {
    text: string
}

export interface MultipleChoiceExerciseAnswer extends BaseExerciseAnswer {
    optionId: string
}

export interface MultipleChoiceOption {
    id: string,
    text: string;
    result?: "success" | "failure";
}

export interface MultipleTextChoiceOptionExercise extends BaseExercise {
    options: MultipleChoiceOption[];
    exerciseAnswer?: MultipleChoiceExerciseAnswer;
}

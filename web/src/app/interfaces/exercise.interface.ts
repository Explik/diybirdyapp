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
    exerciseAnswer?: WrittenExerciseAnswer
}

export interface BaseExerciseAnswer {
    id: string,
    answerType: string
}

export interface WrittenExerciseAnswer extends BaseExerciseAnswer {
    text: string
}
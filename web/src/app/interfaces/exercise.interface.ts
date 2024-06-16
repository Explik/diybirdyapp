export interface BaseExercise {
    id: string;
    exerciseType: string; 
}

export interface WriteSentenceUsingWordExercise extends BaseExercise {
    word: string;
}

export interface TranslateSentenceExercise extends BaseExercise {
    originalSentence: string;
}
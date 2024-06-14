export interface BaseExercise {
    id: string;
    exerciseType: string; 
}

export interface WriteSentenceExercise extends BaseExercise {
    word: string;
}
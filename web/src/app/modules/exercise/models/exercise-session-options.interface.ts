export interface ExerciseSessionOptions {
  textToSpeechEnabled: boolean;
  retypeCorrectAnswerEnabled: boolean;
  initialFlashcardLanguageId: string;
  answerLanguageIds: string[];
}

export const DEFAULT_SESSION_OPTIONS: ExerciseSessionOptions = {
  textToSpeechEnabled: true,
  retypeCorrectAnswerEnabled: false,
  initialFlashcardLanguageId: '',
  answerLanguageIds: []
};
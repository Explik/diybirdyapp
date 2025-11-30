package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsLearnFlashcardsDto extends ExerciseSessionOptionsDto {
    private String[] answerLanguageIds = new String[0];
    private ExerciseSessionOptionsLanguageOptionDto[] availableAnswerLanguage = new ExerciseSessionOptionsLanguageOptionDto[0];

    private boolean includeReviewExercises;
    private boolean includeMultipleChoiceExercises;
    private boolean includeWritingExercises;
    private boolean includeListeningExercises;
    private boolean includePronunciationExercises;

    private boolean retypeCorrectAnswerEnabled;

    public ExerciseSessionOptionsLearnFlashcardsDto() {
        super(ExerciseSessionTypes.LEARN_FLASHCARD);
    }

    public String[] getAnswerLanguageIds() {
        return answerLanguageIds;
    }

    public void setAnswerLanguageIds(String[] answerLanguageIds) {
        this.answerLanguageIds = answerLanguageIds;
    }

    public ExerciseSessionOptionsLanguageOptionDto[] getAvailableAnswerLanguages() {
        return availableAnswerLanguage;
    }

    public void setAvailableAnswerLanguages(ExerciseSessionOptionsLanguageOptionDto[] availableAnswerLanguageIds) {
        this.availableAnswerLanguage = availableAnswerLanguageIds;
    }

    public boolean getIncludeReviewExercises() {
        return includeReviewExercises;
    }

    public void setIncludeReviewExercises(boolean includeReviewExercises) {
        this.includeReviewExercises = includeReviewExercises;
    }

    public boolean getIncludeMultipleChoiceExercises() {
        return includeMultipleChoiceExercises;
    }

    public void setIncludeMultipleChoiceExercises(boolean includeMultipleChoiceExercises) {
        this.includeMultipleChoiceExercises = includeMultipleChoiceExercises;
    }

    public boolean getIncludeWritingExercises() {
        return includeWritingExercises;
    }

    public void setIncludeWritingExercises(boolean includeWritingExercises) {
        this.includeWritingExercises = includeWritingExercises;
    }

    public boolean getIncludeListeningExercises() {
        return includeListeningExercises;
    }

    public void setIncludeListeningExercises(boolean includeListeningExercises) {
        this.includeListeningExercises = includeListeningExercises;
    }

    public boolean getIncludePronunciationExercises() {
        return includePronunciationExercises;
    }

    public void setIncludePronunciationExercises(boolean includePronunciationExercises) {
        this.includePronunciationExercises = includePronunciationExercises;
    }

    public boolean getRetypeCorrectAnswerEnabled() {
        return retypeCorrectAnswerEnabled;
    }

    public void setRetypeCorrectAnswerEnabled(boolean retypeCorrectAnswerEnabled) {
        this.retypeCorrectAnswerEnabled = retypeCorrectAnswerEnabled;
    }

    public record ExerciseTypeOption(String id, String name) { }
}

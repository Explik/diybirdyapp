package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsLearnFlashcardsDto extends ExerciseSessionOptionsDto {
    private String[] multipleChoiceAnswerLanguageIds = new String[0];
    private String[] writingAnswerLanguageIds = new String[0];
    private ExerciseSessionOptionsLanguageOptionDto[] availableAnswerLanguage = new ExerciseSessionOptionsLanguageOptionDto[0];
    private String targetLanguageId;

    private boolean includeReviewExercises;
    private boolean includeMultipleChoiceExercises;
    private boolean includeWritingExercises;
    private boolean includeListeningExercises;
    private boolean includePronunciationExercises;

    private boolean retypeCorrectAnswerEnabled;
    private boolean shuffleFlashcardsEnabled;

    public ExerciseSessionOptionsLearnFlashcardsDto() {
        super(ExerciseSessionTypes.LEARN_FLASHCARD);
    }

    public String[] getMultipleChoiceAnswerLanguageIds() {
        return multipleChoiceAnswerLanguageIds;
    }

    public void setMultipleChoiceAnswerLanguageIds(String[] multipleChoiceAnswerLanguageIds) {
        this.multipleChoiceAnswerLanguageIds = multipleChoiceAnswerLanguageIds;
    }

    public String[] getWritingAnswerLanguageIds() {
        return writingAnswerLanguageIds;
    }

    public void setWritingAnswerLanguageIds(String[] writingAnswerLanguageIds) {
        this.writingAnswerLanguageIds = writingAnswerLanguageIds;
    }

    public ExerciseSessionOptionsLanguageOptionDto[] getAvailableAnswerLanguages() {
        return availableAnswerLanguage;
    }

    public void setAvailableAnswerLanguages(ExerciseSessionOptionsLanguageOptionDto[] availableAnswerLanguageIds) {
        this.availableAnswerLanguage = availableAnswerLanguageIds;
    }

    public String getTargetLanguageId() {
        return targetLanguageId;
    }

    public void setTargetLanguageId(String targetLanguageId) {
        this.targetLanguageId = targetLanguageId;
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

    public boolean getShuffleFlashcardsEnabled() {
        return shuffleFlashcardsEnabled;
    }

    public void setShuffleFlashcardsEnabled(boolean shuffleFlashcardsEnabled) {
        this.shuffleFlashcardsEnabled = shuffleFlashcardsEnabled;
    }

    public record ExerciseTypeOption(String id, String name) { }
}

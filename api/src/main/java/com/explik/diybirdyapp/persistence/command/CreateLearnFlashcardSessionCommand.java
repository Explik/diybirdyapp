package com.explik.diybirdyapp.persistence.command;

import java.util.List;

public class CreateLearnFlashcardSessionCommand implements AtomicCommand {
    private String id;
    private String flashcardDeckId;
    private Boolean retypeCorrectAnswer;
    private Boolean textToSpeechEnabled;
    private List<String> answerLanguageIds;
    private Boolean includeReviewExercises;
    private Boolean includeMultipleChoiceExercises;
    private Boolean includeWritingExercises;
    private Boolean includeListeningExercises;
    private Boolean includePronunciationExercises;
    private List<String> exerciseTypeIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlashcardDeckId() {
        return flashcardDeckId;
    }

    public void setFlashcardDeckId(String flashcardDeckId) {
        this.flashcardDeckId = flashcardDeckId;
    }

    public Boolean getRetypeCorrectAnswer() {
        return retypeCorrectAnswer;
    }

    public void setRetypeCorrectAnswer(Boolean retypeCorrectAnswer) {
        this.retypeCorrectAnswer = retypeCorrectAnswer;
    }

    public Boolean getTextToSpeechEnabled() {
        return textToSpeechEnabled;
    }

    public void setTextToSpeechEnabled(Boolean textToSpeechEnabled) {
        this.textToSpeechEnabled = textToSpeechEnabled;
    }

    public List<String> getAnswerLanguageIds() {
        return answerLanguageIds;
    }

    public void setAnswerLanguageIds(List<String> answerLanguageIds) {
        this.answerLanguageIds = answerLanguageIds;
    }

    public Boolean getIncludeReviewExercises() {
        return includeReviewExercises;
    }

    public void setIncludeReviewExercises(Boolean includeReviewExercises) {
        this.includeReviewExercises = includeReviewExercises;
    }

    public Boolean getIncludeMultipleChoiceExercises() {
        return includeMultipleChoiceExercises;
    }

    public void setIncludeMultipleChoiceExercises(Boolean includeMultipleChoiceExercises) {
        this.includeMultipleChoiceExercises = includeMultipleChoiceExercises;
    }

    public Boolean getIncludeWritingExercises() {
        return includeWritingExercises;
    }

    public void setIncludeWritingExercises(Boolean includeWritingExercises) {
        this.includeWritingExercises = includeWritingExercises;
    }

    public Boolean getIncludeListeningExercises() {
        return includeListeningExercises;
    }

    public void setIncludeListeningExercises(Boolean includeListeningExercises) {
        this.includeListeningExercises = includeListeningExercises;
    }

    public Boolean getIncludePronunciationExercises() {
        return includePronunciationExercises;
    }

    public void setIncludePronunciationExercises(Boolean includePronunciationExercises) {
        this.includePronunciationExercises = includePronunciationExercises;
    }

    public List<String> getExerciseTypeIds() {
        return exerciseTypeIds;
    }

    public void setExerciseTypeIds(List<String> exerciseTypeIds) {
        this.exerciseTypeIds = exerciseTypeIds;
    }
}

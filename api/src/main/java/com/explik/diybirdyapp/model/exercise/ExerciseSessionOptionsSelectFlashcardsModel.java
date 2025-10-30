package com.explik.diybirdyapp.model.exercise;

public class ExerciseSessionOptionsSelectFlashcardsModel extends ExerciseSessionOptionsModel {
    private boolean textToSpeechEnabled;
    private String initialFlashcardLanguageId;
    private String[] availableFlashcardLanguageIds;

    public boolean getTextToSpeechEnabled() {
        return textToSpeechEnabled;
    }

    public void setTextToSpeechEnabled(boolean textToSpeechEnabled) {
        this.textToSpeechEnabled = textToSpeechEnabled;
    }

    public String getInitialFlashcardLanguageId() {
        return initialFlashcardLanguageId;
    }

    public void setInitialFlashcardLanguageId(String initialFlashcardLanguageId) {
        this.initialFlashcardLanguageId = initialFlashcardLanguageId;
    }

    public String[] getAvailableFlashcardLanguageIds() {
        return availableFlashcardLanguageIds;
    }

    public void setAvailableFlashcardLanguageIds(String[] availableFlashcardLanguageIds) {
        this.availableFlashcardLanguageIds = availableFlashcardLanguageIds;
    }
}

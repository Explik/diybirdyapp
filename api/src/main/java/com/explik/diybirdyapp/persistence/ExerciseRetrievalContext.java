package com.explik.diybirdyapp.persistence;

public class ExerciseRetrievalContext {
    private boolean textToSpeechEnabled = false;
    private String initialFlashcardLanguageId = null;

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

    public static ExerciseRetrievalContext createDefault() {
        return new ExerciseRetrievalContext();
    }
}

package com.explik.diybirdyapp.persistence;

public class ExerciseRetrievalContext {
    private boolean textToSpeechEnabled = false;
    private String initialFlashcardLanguageId = null;
    private boolean initiallyHideOptions = false;

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

    public boolean getInitiallyHideOptions() {
        return initiallyHideOptions;
    }

    public void setInitiallyHideOptions(boolean initiallyHideOptions) {
        this.initiallyHideOptions = initiallyHideOptions;
    }

    public static ExerciseRetrievalContext createDefault() {
        return new ExerciseRetrievalContext();
    }
}

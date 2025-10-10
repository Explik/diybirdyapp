package com.explik.diybirdyapp.persistence;

public class ExerciseRetrievalContext {
    private boolean textToSpeechEnabled = false;

    public boolean getTextToSpeechEnabled() {
        return textToSpeechEnabled;
    }

    public void setTextToSpeechEnabled(boolean textToSpeechEnabled) {
        this.textToSpeechEnabled = textToSpeechEnabled;
    }

    public static ExerciseRetrievalContext createDefault() {
        return new ExerciseRetrievalContext();
    }
}

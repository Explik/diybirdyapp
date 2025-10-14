package com.explik.diybirdyapp.model.exercise;

public class ExerciseSessionOptionsModel {
    private boolean textToSpeechEnabled;
    private boolean retypeCorrectAnswerEnabled;
    private String initialFlashcardLanguageId;
    private String [] answerLanguageIds = new String[0];

    public boolean getTextToSpeechEnabled() {
        return textToSpeechEnabled;
    }

    public void setTextToSpeechEnabled(boolean textToSpeechEnabled) {
        this.textToSpeechEnabled = textToSpeechEnabled;
    }

    public boolean getRetypeCorrectAnswerEnabled() {
        return retypeCorrectAnswerEnabled;
    }

    public void setRetypeCorrectAnswerEnabled(boolean retypeCorrectAnswerEnabled) {
        this.retypeCorrectAnswerEnabled = retypeCorrectAnswerEnabled;
    }

    public String getInitialFlashcardLanguageId() {
        return initialFlashcardLanguageId;
    }

    public void setInitialFlashcardLanguageId(String initialFlashcardLanguageId) {
        this.initialFlashcardLanguageId = initialFlashcardLanguageId;
    }

    public String[] getAnswerLanguageIds() {
        return answerLanguageIds;
    }

    public void setAnswerLanguageIds(String[] answerLanguageIds) {
        this.answerLanguageIds = answerLanguageIds;
    }
}

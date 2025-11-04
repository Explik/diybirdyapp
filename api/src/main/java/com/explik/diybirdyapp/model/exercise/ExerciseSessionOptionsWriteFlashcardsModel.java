package com.explik.diybirdyapp.model.exercise;

public class ExerciseSessionOptionsWriteFlashcardsModel extends ExerciseSessionOptionsModel {
    private boolean textToSpeechEnabled;
    private String answerLanguageId;
    private String[] availableAnswerLanguageIds;
    private boolean retypeCorrectAnswerEnabled;

    public boolean getTextToSpeechEnabled() {
        return textToSpeechEnabled;
    }

    public void setTextToSpeechEnabled(boolean textToSpeechEnabled) {
        this.textToSpeechEnabled = textToSpeechEnabled;
    }

    public String getAnswerLanguageId() {
        return answerLanguageId;
    }

    public void setAnswerLanguageId(String answerLanguageId) {
        this.answerLanguageId = answerLanguageId;
    }

    public String[] getAvailableAnswerLanguageIds() {
        return availableAnswerLanguageIds;
    }

    public void setAvailableAnswerLanguageIds(String[] availableAnswerLanguageIds) {
        this.availableAnswerLanguageIds = availableAnswerLanguageIds;
    }

    public boolean getRetypeCorrectAnswerEnabled() {
        return retypeCorrectAnswerEnabled;
    }

    public void setRetypeCorrectAnswerEnabled(boolean retypeCorrectAnswerEnabled) {
        this.retypeCorrectAnswerEnabled = retypeCorrectAnswerEnabled;
    }
}

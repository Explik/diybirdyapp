package com.explik.diybirdyapp.model.exercise;

public class ExerciseSessionOptionsLearnFlashcardModel extends ExerciseSessionOptionsModel {
    private String[] answerLanguageIds;
    private String[] availableAnswerLanguageIds;
    private boolean retypeCorrectAnswerEnabled;

    public String[] getAnswerLanguageIds() {
        return answerLanguageIds;
    }

    public void setAnswerLanguageIds(String[] answerLanguageIds) {
        this.answerLanguageIds = answerLanguageIds;
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

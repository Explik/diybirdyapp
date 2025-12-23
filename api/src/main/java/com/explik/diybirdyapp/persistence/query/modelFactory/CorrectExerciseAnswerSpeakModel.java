package com.explik.diybirdyapp.persistence.query.modelFactory;

import java.util.List;

public class CorrectExerciseAnswerSpeakModel {
    private String languageCode;
    private List<String> correctTextValues;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public List<String> getCorrectTextValues() {
        return correctTextValues;
    }

    public void setCorrectTextValues(List<String> correctTextValues) {
        this.correctTextValues = correctTextValues;
    }
}

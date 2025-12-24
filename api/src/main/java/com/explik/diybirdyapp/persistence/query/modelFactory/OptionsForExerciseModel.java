package com.explik.diybirdyapp.persistence.query.modelFactory;

import java.util.List;

public class OptionsForExerciseModel {
    private String correctOptionId;
    private List<String> incorrectOptionIds;
    private List<String> allOptionIds;

    public String getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(String correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public List<String> getIncorrectOptionIds() {
        return incorrectOptionIds;
    }

    public void setIncorrectOptionIds(List<String> incorrectOptionIds) {
        this.incorrectOptionIds = incorrectOptionIds;
    }

    public List<String> getAllOptionIds() {
        return allOptionIds;
    }

    public void setAllOptionIds(List<String> allOptionIds) {
        this.allOptionIds = allOptionIds;
    }
}

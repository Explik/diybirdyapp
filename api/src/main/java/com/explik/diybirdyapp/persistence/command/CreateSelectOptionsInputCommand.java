package com.explik.diybirdyapp.persistence.command;

import java.util.List;

/**
 * Domain command to create select options (multiple choice) input for an exercise.
 * Users must select the correct option(s) from a list.
 */
public class CreateSelectOptionsInputCommand implements AtomicCommand {
    private List<String> correctOptionIds;
    private List<String> incorrectOptionIds;

    public List<String> getCorrectOptionIds() {
        return correctOptionIds;
    }

    public void setCorrectOptionIds(List<String> correctOptionIds) {
        this.correctOptionIds = correctOptionIds;
    }

    public List<String> getIncorrectOptionIds() {
        return incorrectOptionIds;
    }

    public void setIncorrectOptionIds(List<String> incorrectOptionIds) {
        this.incorrectOptionIds = incorrectOptionIds;
    }
}

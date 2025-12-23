package com.explik.diybirdyapp.persistence.command;

/**
 * Domain command to create write text input for an exercise.
 * Users must type the correct text answer.
 */
public class CreateWriteTextInputCommand implements AtomicCommand {
    private String correctOptionId;

    public String getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(String correctOptionId) {
        this.correctOptionId = correctOptionId;
    }
}

package com.explik.diybirdyapp.persistence.command;

/**
 * Domain command to create record audio input for an exercise.
 * Users must record audio as their answer.
 */
public class CreateRecordAudioInputCommand implements AtomicCommand {
    private String correctOptionId;

    public String getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(String correctOptionId) {
        this.correctOptionId = correctOptionId;
    }
}

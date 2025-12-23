package com.explik.diybirdyapp.persistence.command;

/**
 * Command to handle a pronunciation exercise answer for a flashcard.
 * Creates a pronunciation vertex linked to the exercise's text content.
 */
public class HandleFlashcardPronunciationExerciseAnswerCommand implements AtomicCommand {
    private String exerciseId;
    private String answerId;

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }
}

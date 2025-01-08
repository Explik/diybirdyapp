package com.explik.diybirdyapp.persistence.command;

public class HandleFlashcardPronunciationExerciseAnswerCommand {
    private final String exerciseId;
    private final String answerId;

    public HandleFlashcardPronunciationExerciseAnswerCommand(String exerciseId, String answerId) {
        this.exerciseId = exerciseId;
        this.answerId = answerId;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public String getAnswerId() {
        return answerId;
    }
}

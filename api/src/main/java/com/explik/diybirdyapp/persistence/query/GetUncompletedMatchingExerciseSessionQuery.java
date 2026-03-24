package com.explik.diybirdyapp.persistence.query;

public class GetUncompletedMatchingExerciseSessionQuery {
    private String type;
    private String flashcardDeckId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlashcardDeckId() {
        return flashcardDeckId;
    }

    public void setFlashcardDeckId(String flashcardDeckId) {
        this.flashcardDeckId = flashcardDeckId;
    }
}

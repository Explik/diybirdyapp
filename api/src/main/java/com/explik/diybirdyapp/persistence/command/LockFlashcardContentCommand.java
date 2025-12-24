package com.explik.diybirdyapp.persistence.command;

/**
 * Command to lock flashcard content, making it static/immutable.
 * Once locked, the flashcard's content properties cannot be updated.
 */
public class LockFlashcardContentCommand implements AtomicCommand {
    private String flashcardId;

    public String getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(String flashcardId) {
        this.flashcardId = flashcardId;
    }
}

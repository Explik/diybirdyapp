package com.explik.diybirdyapp.persistence.command;

/**
 * Command to extract words from a flashcard's text content.
 * Creates word vertices for each unique word found in the flashcard's sides.
 */
public class ExtractWordsFromFlashcardCommand implements AtomicCommand {
    private String flashcardId;

    public String getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(String flashcardId) {
        this.flashcardId = flashcardId;
    }
}

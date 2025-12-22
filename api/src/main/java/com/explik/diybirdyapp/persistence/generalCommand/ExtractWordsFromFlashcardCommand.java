package com.explik.diybirdyapp.persistence.generalCommand;

public class ExtractWordsFromFlashcardCommand {
    private final String flashcardId;

    public ExtractWordsFromFlashcardCommand(String flashcardId) {
        this.flashcardId = flashcardId;
    }

    public String getFlashcardId() {
        return flashcardId;
    }
}
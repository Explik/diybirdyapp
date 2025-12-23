package com.explik.diybirdyapp.persistence.command;

/**
 * Command to generate audio pronunciation for flashcard text content using text-to-speech.
 * Generates audio for both left and right sides of the flashcard if they contain text.
 */
public class GenerateAudioForFlashcardCommand implements AtomicCommand {
    private String flashcardId;
    private boolean failOnMissingVoice;

    public String getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(String flashcardId) {
        this.flashcardId = flashcardId;
    }

    public boolean getFailOnMissingVoice() {
        return failOnMissingVoice;
    }

    public void setFailOnMissingVoice(boolean failOnMissingVoice) {
        this.failOnMissingVoice = failOnMissingVoice;
    }
}

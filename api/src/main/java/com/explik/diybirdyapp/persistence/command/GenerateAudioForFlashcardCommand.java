package com.explik.diybirdyapp.persistence.command;

public class GenerateAudioForFlashcardCommand {
    private final String flashcardId;

    private boolean failOnMissingVoice;

    public GenerateAudioForFlashcardCommand(String flashcardId) {
        this.flashcardId = flashcardId;
    }

    public String getFlashcardId() {
        return flashcardId;
    }

    public boolean getFailOnMissingVoice() {
        return failOnMissingVoice;
    }

    public void setFailOnMissingVoice(boolean failOnMissingVoice) {
        this.failOnMissingVoice = failOnMissingVoice;
    }
}

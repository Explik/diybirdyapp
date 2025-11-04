package com.explik.diybirdyapp.persistence.command;

public class GenerateAudioForFlashcardCommand {
    private final String flashcardId;
    private final String flashcardSide;
    private boolean failOnMissingVoice;

    public GenerateAudioForFlashcardCommand(String flashcardId) {
        this.flashcardId = flashcardId;
        this.flashcardSide = null;
    }

    public GenerateAudioForFlashcardCommand(String flashcardId, String flashcardSide) {
        this.flashcardId = flashcardId;
        this.flashcardSide = flashcardSide;
    }

    public String getFlashcardId() {
        return flashcardId;
    }

    public String getFlashcardSide() {
        return flashcardSide;
    }

    public boolean getFailOnMissingVoice() {
        return failOnMissingVoice;
    }

    public void setFailOnMissingVoice(boolean failOnMissingVoice) {
        this.failOnMissingVoice = failOnMissingVoice;
    }
}

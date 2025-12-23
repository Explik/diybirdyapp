package com.explik.diybirdyapp.persistence.command;

public class CreateReviewFlashcardSessionCommand implements AtomicCommand {
    private String id;
    private String flashcardDeckId;
    private Boolean textToSpeechEnabled;
    private String initialFlashcardLanguageId;
    private String algorithm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlashcardDeckId() {
        return flashcardDeckId;
    }

    public void setFlashcardDeckId(String flashcardDeckId) {
        this.flashcardDeckId = flashcardDeckId;
    }

    public Boolean getTextToSpeechEnabled() {
        return textToSpeechEnabled;
    }

    public void setTextToSpeechEnabled(Boolean textToSpeechEnabled) {
        this.textToSpeechEnabled = textToSpeechEnabled;
    }

    public String getInitialFlashcardLanguageId() {
        return initialFlashcardLanguageId;
    }

    public void setInitialFlashcardLanguageId(String initialFlashcardLanguageId) {
        this.initialFlashcardLanguageId = initialFlashcardLanguageId;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}

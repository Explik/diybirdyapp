package com.explik.diybirdyapp.persistence.command;

public class CreateSelectFlashcardSessionCommand implements AtomicCommand {
    private String id;
    private String flashcardDeckId;
    private Boolean textToSpeechEnabled;
    private String initialFlashcardLanguageId;
    private Boolean initiallyHideOptions;
    private Boolean shuffleFlashcards;

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

    public Boolean getInitiallyHideOptions() {
        return initiallyHideOptions;
    }

    public void setInitiallyHideOptions(Boolean initiallyHideOptions) {
        this.initiallyHideOptions = initiallyHideOptions;
    }

    public Boolean getShuffleFlashcards() {
        return shuffleFlashcards;
    }

    public void setShuffleFlashcards(Boolean shuffleFlashcards) {
        this.shuffleFlashcards = shuffleFlashcards;
    }
}

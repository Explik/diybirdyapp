package com.explik.diybirdyapp.persistence.command;

public class DeleteFlashcardDeckCommand implements AtomicCommand {
    private String userId;
    private String deckId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }
}

package com.explik.diybirdyapp.model.content;

public class FlashcardModel {
    private String id;
    private String deckId;
    private Integer deckOrder;

    private FlashcardContentModel frontContent;
    private FlashcardContentModel backContent;

    public FlashcardModel() {}

    public FlashcardModel(String id, String deckId, FlashcardContentModel frontContent, FlashcardContentModel backContent) {
        this.id = id;
        this.deckId = deckId;
        this.frontContent = frontContent;
        this.backContent = backContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public Integer getDeckOrder() {
        return deckOrder;
    }

    public void setDeckOrder(Integer deckOrder) {
        this.deckOrder = deckOrder;
    }

    public FlashcardContentModel getFrontContent() {
        return frontContent;
    }

    public void setFrontContent(FlashcardContentModel frontContent) {
        this.frontContent = frontContent;
    }

    public FlashcardContentModel getBackContent() {
        return backContent;
    }

    public void setBackContent(FlashcardContentModel backContent) {
        this.backContent = backContent;
    }
}

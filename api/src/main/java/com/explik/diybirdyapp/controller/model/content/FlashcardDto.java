package com.explik.diybirdyapp.controller.model.content;

import jakarta.validation.constraints.NotBlank;

public class FlashcardDto {
    @NotBlank(message = "id.required")
    private String id;

    private String deckId;

    private Integer deckOrder;

    private FlashcardContentDto frontContent;

    private FlashcardContentDto backContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeckId() {
        return deckId;
    }

    public Integer getDeckOrder() {
        return deckOrder;
    }

    public void setDeckOrder(Integer deckOrder) {
        this.deckOrder = deckOrder;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public FlashcardContentDto getFrontContent() {
        return frontContent;
    }

    public void setFrontContent(FlashcardContentDto frontContent) {
        this.frontContent = frontContent;
    }

    public FlashcardContentDto getBackContent() {
        return backContent;
    }

    public void setBackContent(FlashcardContentDto backContent) {
        this.backContent = backContent;
    }
}

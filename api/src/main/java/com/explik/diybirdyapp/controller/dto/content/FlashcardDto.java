package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.model.content.FlashcardLanguageModel;

public class FlashcardDto {
    private String id;
    private String deckId;

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

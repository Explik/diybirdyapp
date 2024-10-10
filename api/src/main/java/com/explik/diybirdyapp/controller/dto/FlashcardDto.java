package com.explik.diybirdyapp.controller.dto;

import com.explik.diybirdyapp.model.FlashcardLanguageModel;

public class FlashcardDto {
    private String id;
    private String deckId;

    private String leftValue;
    private FlashcardLanguageModel leftLanguage;

    private String rightValue;
    private FlashcardLanguageModel rightLanguage;

    public FlashcardDto() {}

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

    // Getter for leftValue
    public String getLeftValue() {
        return leftValue;
    }

    // Setter for leftValue
    public void setLeftValue(String leftValue) {
        this.leftValue = leftValue;
    }

    // Getter for leftLanguage
    public FlashcardLanguageModel getLeftLanguage() {
        return leftLanguage;
    }

    // Setter for leftLanguage
    public void setLeftLanguage(FlashcardLanguageModel leftLanguage) {
        this.leftLanguage = leftLanguage;
    }

    // Getter for rightValue
    public String getRightValue() {
        return rightValue;
    }

    // Setter for rightValue
    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }

    // Getter for rightLanguage
    public FlashcardLanguageModel getRightLanguage() {
        return rightLanguage;
    }

    // Setter for rightLanguage
    public void setRightLanguage(FlashcardLanguageModel rightLanguage) {
        this.rightLanguage = rightLanguage;
    }
}

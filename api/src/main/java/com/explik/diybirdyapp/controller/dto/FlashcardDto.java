package com.explik.diybirdyapp.controller.dto;

import com.explik.diybirdyapp.graph.model.LanguageModel;

public class FlashcardDto {
    private String id;

    private String leftValue;
    private LanguageModel leftLanguage;

    private String rightValue;
    private LanguageModel rightLanguage;

    public FlashcardDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public LanguageModel getLeftLanguage() {
        return leftLanguage;
    }

    // Setter for leftLanguage
    public void setLeftLanguage(LanguageModel leftLanguage) {
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
    public LanguageModel getRightLanguage() {
        return rightLanguage;
    }

    // Setter for rightLanguage
    public void setRightLanguage(LanguageModel rightLanguage) {
        this.rightLanguage = rightLanguage;
    }
}

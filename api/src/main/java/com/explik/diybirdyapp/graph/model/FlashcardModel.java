package com.explik.diybirdyapp.graph.model;

public class FlashcardModel {
    private long id;

    private String leftValue;
    private LanguageModel leftLanguage;

    private String rightValue;
    private LanguageModel rightLanguage;

    // Default constructor
    public FlashcardModel() {}

    // Parameterized constructor
    public FlashcardModel(long id, String leftValue, LanguageModel leftLanguage, String rightValue, LanguageModel rightLanguage) {
        this.id = id;
        this.leftValue = leftValue;
        this.leftLanguage = leftLanguage;
        this.rightValue = rightValue;
        this.rightLanguage = rightLanguage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Override
    public String toString() {
        return "FlashcardModel{" +
            "leftValue='" + leftValue + '\'' +
            ", leftLanguage=" + leftLanguage +
            ", rightValue='" + rightValue + '\'' +
            ", rightLanguage=" + rightLanguage +
            '}';
    }
}

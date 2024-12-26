package com.explik.diybirdyapp.model;

public class FlashcardLanguageModel {
    private String id;
    private String abbreviation;
    private String name;

    // Default constructor
    public FlashcardLanguageModel() {}

    // Id only constructor
    public FlashcardLanguageModel(String id) {
        this.id = id;
    }

    // Parameterized constructor
    public FlashcardLanguageModel(String id, String abbreviation, String name) {
        this.id = id;
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter for abbreviation
    public String getAbbreviation() {
        return abbreviation;
    }

    // Setter for abbreviation
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FlashcardLanguageModel{" +
            "abbreviation='" + abbreviation + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
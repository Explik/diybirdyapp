package com.explik.diybirdyapp.graph.model;

public class LanguageModel {
    private String abbreviation;
    private String name;

    // Default constructor
    public LanguageModel() {}

    // Parameterized constructor
    public LanguageModel(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
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
        return "LanguageModel{" +
            "abbreviation='" + abbreviation + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
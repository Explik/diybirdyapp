package com.explik.diybirdyapp.model.content;

public class FlashcardLanguageModel {
    private String id;
    private String name;
    private String isoCode;

    public FlashcardLanguageModel() {}

    public FlashcardLanguageModel(String id) {
        this.id = id;
    }

    public FlashcardLanguageModel(String id, String isoCode, String name) {
        this.id = id;
        this.name = name;
        this.isoCode = isoCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FlashcardLanguageModel{" +
            "isoCode='" + isoCode + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
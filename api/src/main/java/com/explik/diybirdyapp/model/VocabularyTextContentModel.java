package com.explik.diybirdyapp.model;

public class VocabularyTextContentModel {
    public VocabularyTextContentModel() {}

    public VocabularyTextContentModel(String text) {
        this.text = text;
    }

    private String text;

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }
}

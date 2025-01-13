package com.explik.diybirdyapp.model;

public class VocabularyTextContentModel {
    public VocabularyTextContentModel() {}

    public VocabularyTextContentModel(String text) {
        this.text = text;
    }

    private String text;
    private String pronunciationUrl;

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getPronunciationUrl() { return pronunciationUrl; }

    public void setPronunciationUrl(String pronunciationUrl) { this.pronunciationUrl = pronunciationUrl; }
}

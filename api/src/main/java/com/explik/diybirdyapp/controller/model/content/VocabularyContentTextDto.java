package com.explik.diybirdyapp.controller.model.content;

public class VocabularyContentTextDto {
    private String text;
    private String pronunciationUrl;

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getPronunciationUrl() { return pronunciationUrl; }

    public void setPronunciationUrl(String pronunciationUrl) { this.pronunciationUrl = pronunciationUrl; }
}

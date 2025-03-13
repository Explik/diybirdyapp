package com.explik.diybirdyapp.model.content;

import jakarta.validation.constraints.NotNull;

public class VocabularyTextContentModel {
    public VocabularyTextContentModel() {}

    public VocabularyTextContentModel(String text) {
        this.text = text;
    }

    @NotNull
    private String text;

    @NotNull
    private String pronunciationUrl;

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getPronunciationUrl() { return pronunciationUrl; }

    public void setPronunciationUrl(String pronunciationUrl) { this.pronunciationUrl = pronunciationUrl; }
}

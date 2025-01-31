package com.explik.diybirdyapp.model.content;

public class FlashcardContentTextModel extends FlashcardContentModel {
    private String text;
    private String languageId;

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getLanguageId() { return languageId; }

    public void setLanguageId(String languageId) { this.languageId = languageId; }
}

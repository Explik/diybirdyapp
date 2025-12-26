package com.explik.diybirdyapp.model.admin;

public class TranslationRequestDto {
    private String sourceLanguageId;
    private String targetLanguageId;
    private String text;

    public String getSourceLanguageId() {
        return sourceLanguageId;
    }

    public void setSourceLanguageId(String sourceLanguageId) {
        this.sourceLanguageId = sourceLanguageId;
    }

    public String getTargetLanguageId() {
        return targetLanguageId;
    }

    public void setTargetLanguageId(String targetLanguageId) {
        this.targetLanguageId = targetLanguageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

package com.explik.diybirdyapp.model.admin;

public class GoogleTranslationConfigModel {
    private String sourceLanguageCode;
    private String targetLanguageCode;

    public GoogleTranslationConfigModel(String sourceLanguageCode, String targetLanguageCode) {
        this.sourceLanguageCode = sourceLanguageCode;
        this.targetLanguageCode = targetLanguageCode;
    }

    public String getSourceLanguageCode() {
        return sourceLanguageCode;
    }

    public void setSourceLanguageCode(String sourceLanguageCode) {
        this.sourceLanguageCode = sourceLanguageCode;
    }

    public String getTargetLanguageCode() {
        return targetLanguageCode;
    }

    public void setTargetLanguageCode(String targetLanguageCode) {
        this.targetLanguageCode = targetLanguageCode;
    }
}

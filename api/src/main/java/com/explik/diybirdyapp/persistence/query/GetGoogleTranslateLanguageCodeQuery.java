package com.explik.diybirdyapp.persistence.query;

public class GetGoogleTranslateLanguageCodeQuery {
    private String sourceLanguageId;
    private String targetLanguageId;

    public GetGoogleTranslateLanguageCodeQuery(String sourceLanguageId, String targetLanguageId) {
        this.sourceLanguageId = sourceLanguageId;
        this.targetLanguageId = targetLanguageId;
    }

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
}

package com.explik.diybirdyapp.persistence.command;

public class CreateSpeechToTextConfigVertexCommand implements AtomicCommand {
    private String id;
    private String languageCode;
    private String languageVertexId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageVertexId() {
        return languageVertexId;
    }

    public void setLanguageVertexId(String languageVertexId) {
        this.languageVertexId = languageVertexId;
    }
}

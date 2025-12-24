package com.explik.diybirdyapp.persistence.command;

public class CreateTextToSpeechConfigVertexCommand implements AtomicCommand {
    private String id;
    private String languageCode;
    private String voiceName;
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

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public String getLanguageVertexId() {
        return languageVertexId;
    }

    public void setLanguageVertexId(String languageVertexId) {
        this.languageVertexId = languageVertexId;
    }
}

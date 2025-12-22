package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;

public class CreateTextToSpeechConfigVertexCommand implements AtomicCommand {
    private String id;
    private String languageCode;
    private String voiceName;
    private LanguageVertex languageVertex;

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

    public LanguageVertex getLanguageVertex() {
        return languageVertex;
    }

    public void setLanguageVertex(LanguageVertex languageVertex) {
        this.languageVertex = languageVertex;
    }
}

package com.explik.diybirdyapp.model;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class VoiceModel {
    private String languageId;
    private String languageName;

    private String voiceId;
    private String voiceName;
    private String voiceLanguageCode;

    public VoiceModel() {}

    public VoiceModel(String languageId, String languageName, String voiceId, String voiceName, String voiceLanguageCode) {
        this.languageId = languageId;
        this.languageName = languageName;
        this.voiceId = voiceId;
        this.voiceName = voiceName;
        this.voiceLanguageCode = voiceLanguageCode;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public String getVoiceLanguageCode() {
        return voiceLanguageCode;
    }

    public void setVoiceLanguageCode(String voiceLanguageCode) {
        this.voiceLanguageCode = voiceLanguageCode;
    }
}

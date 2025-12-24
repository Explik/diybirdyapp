package com.explik.diybirdyapp.model.internal;

public class GoogleTextToSpeechVoiceModel extends VoiceModel {
    private String voiceId;
    private String voiceName;
    private String voiceLanguageCode;

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

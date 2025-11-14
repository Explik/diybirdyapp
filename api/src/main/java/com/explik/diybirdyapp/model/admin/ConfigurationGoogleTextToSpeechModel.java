package com.explik.diybirdyapp.model.admin;

public class ConfigurationGoogleTextToSpeechModel extends ConfigurationModel {
    private String languageCode;
    private String voiceName;

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
}

package com.explik.diybirdyapp.controller.dto.admin;

import com.explik.diybirdyapp.ConfigurationTypes;

public class ConfigurationGoogleTextToSpeechDto extends ConfigurationDto {
    private String languageCode;
    private String voiceName;

    public ConfigurationGoogleTextToSpeechDto() {
        super(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
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
}

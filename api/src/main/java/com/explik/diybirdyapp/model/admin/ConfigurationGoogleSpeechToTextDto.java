package com.explik.diybirdyapp.model.admin;

import com.explik.diybirdyapp.ConfigurationTypes;

public class ConfigurationGoogleSpeechToTextDto extends ConfigurationDto {
    public ConfigurationGoogleSpeechToTextDto() {
        super(ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT);
    }

    private String languageCode;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}

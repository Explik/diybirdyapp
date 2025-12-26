package com.explik.diybirdyapp.model.admin;

import com.explik.diybirdyapp.ConfigurationTypes;

public class ConfigurationMicrosoftTextToSpeechDto extends ConfigurationDto {
    private String voiceName;

    public ConfigurationMicrosoftTextToSpeechDto() {
        super(ConfigurationTypes.MICROSOFT_TEXT_TO_SPEECH);
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }
}

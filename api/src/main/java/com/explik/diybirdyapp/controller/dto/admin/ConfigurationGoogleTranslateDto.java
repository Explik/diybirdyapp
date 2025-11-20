package com.explik.diybirdyapp.controller.dto.admin;

import com.explik.diybirdyapp.ConfigurationTypes;

public class ConfigurationGoogleTranslateDto extends ConfigurationDto {
    private String languageCode;

    public ConfigurationGoogleTranslateDto() {
        super(ConfigurationTypes.GOOGLE_TRANSLATE);
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}

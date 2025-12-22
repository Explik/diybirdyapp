package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;

public class CreateLanguageConfigCommand {
    private String languageId;
    private ConfigurationDto config;

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public ConfigurationDto getConfig() {
        return config;
    }

    public void setConfig(ConfigurationDto config) {
        this.config = config;
    }
}

package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;

public class CreateLanguageConfigCommand implements AtomicCommand {
    private String languageId;
    private ConfigurationDto config;
    private String resultId;

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

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
}

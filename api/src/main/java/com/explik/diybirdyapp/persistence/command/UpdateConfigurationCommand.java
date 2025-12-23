package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;

public class UpdateConfigurationCommand implements AtomicCommand {
    private ConfigurationDto configuration;
    private String resultId;

    public ConfigurationDto getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationDto configuration) {
        this.configuration = configuration;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
}

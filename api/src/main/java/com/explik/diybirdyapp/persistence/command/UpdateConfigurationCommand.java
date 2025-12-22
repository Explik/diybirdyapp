package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;

public class UpdateConfigurationCommand {
    private ConfigurationDto configuration;

    public ConfigurationDto getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationDto configuration) {
        this.configuration = configuration;
    }
}

package com.explik.diybirdyapp.persistence.command;

public class DeleteConfigurationCommand implements AtomicCommand {
    private String configId;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }
}

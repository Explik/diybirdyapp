package com.explik.diybirdyapp.persistence.command;

public class DetachLanguageConfigCommand implements AtomicCommand {
    private String languageId;
    private String configId;

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }
}

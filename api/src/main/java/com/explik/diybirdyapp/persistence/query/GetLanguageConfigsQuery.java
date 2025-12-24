package com.explik.diybirdyapp.persistence.query;

public class GetLanguageConfigsQuery {
    private String languageId;
    private String configurationType;

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(String configurationType) {
        this.configurationType = configurationType;
    }
}

package com.explik.diybirdyapp.persistence.command;

public class CreateTextContentVertexCommand implements AtomicCommand {
    private String id;
    private String value;
    private String languageId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguage(String languageId) {
        this.languageId = languageId;
    }
}

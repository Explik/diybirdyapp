package com.explik.diybirdyapp.persistence.command;

public class CreateWordVertexCommand implements AtomicCommand {
    private String id;
    private String value;
    private String mainExampleId;
    private String languageVertexId;

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

    public String getMainExampleId() {
        return mainExampleId;
    }

    public void setMainExample(String mainExampleId) {
        this.mainExampleId = mainExampleId;
    }

    public String getLanguageVertexId() {
        return languageVertexId;
    }

    public void setLanguageVertex(String languageVertexId) {
        this.languageVertexId = languageVertexId;
    }
}

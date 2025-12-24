package com.explik.diybirdyapp.persistence.command;

public class CreateVideoContentVertexCommand implements AtomicCommand {
    private String id;
    private String url;
    private String languageVertexId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguageVertexId() {
        return languageVertexId;
    }

    public void setLanguageVertexId(String languageVertexId) {
        this.languageVertexId = languageVertexId;
    }
}

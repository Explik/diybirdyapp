package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;

public class CreateVideoContentVertexCommand implements AtomicCommand {
    private String id;
    private String url;
    private LanguageVertex languageVertex;

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

    public LanguageVertex getLanguageVertex() {
        return languageVertex;
    }

    public void setLanguageVertex(LanguageVertex languageVertex) {
        this.languageVertex = languageVertex;
    }
}

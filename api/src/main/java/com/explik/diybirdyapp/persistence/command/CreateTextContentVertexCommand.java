package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;

public class CreateTextContentVertexCommand implements AtomicCommand {
    private String id;
    private String value;
    private LanguageVertex language;

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

    public LanguageVertex getLanguage() {
        return language;
    }

    public void setLanguage(LanguageVertex language) {
        this.language = language;
    }
}

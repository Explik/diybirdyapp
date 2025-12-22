package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;

public class CreateWordVertexCommand implements AtomicCommand {
    private String id;
    private String value;
    private TextContentVertex mainExample;
    private LanguageVertex languageVertex;

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

    public TextContentVertex getMainExample() {
        return mainExample;
    }

    public void setMainExample(TextContentVertex mainExample) {
        this.mainExample = mainExample;
    }

    public LanguageVertex getLanguageVertex() {
        return languageVertex;
    }

    public void setLanguageVertex(LanguageVertex languageVertex) {
        this.languageVertex = languageVertex;
    }
}

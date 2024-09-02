package com.explik.diybirdyapp.graph.vertex;

import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import com.syncleus.ferma.annotations.Property;
import com.syncleus.ferma.AbstractVertexFrame;

public abstract class FlashcardLanguageVertex extends AbstractVertexFrame {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    @Property("abbreviation")
    public abstract String getAbbreviation();

    @Property("abbreviation")
    public abstract void setAbbreviation(String abbreviation);

    @Property("name")
    public abstract String getName();

    @Property("name")
    public abstract void setName(String name);

    public FlashcardLanguageModel toFlashcardLanguageModel() {
        var model = new FlashcardLanguageModel();
        model.setId(getId());
        model.setAbbreviation(getAbbreviation());
        model.setName(getName());
        return model;
    }
}

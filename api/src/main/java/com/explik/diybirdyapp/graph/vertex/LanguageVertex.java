package com.explik.diybirdyapp.graph.vertex;

import com.explik.diybirdyapp.graph.model.LanguageModel;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.Property;
import com.syncleus.ferma.AbstractVertexFrame;

public abstract class LanguageVertex extends AbstractVertexFrame {
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

    public void MapTo(LanguageModel model) {
        model.setId(this.getId());
        model.setAbbreviation(this.getAbbreviation());
        model.setName(this.getName());
    }

    public void MapFrom(LanguageModel model) {
        this.setId(model.getId());
        this.setAbbreviation(model.getAbbreviation());
        this.setName(model.getName());
    }
}

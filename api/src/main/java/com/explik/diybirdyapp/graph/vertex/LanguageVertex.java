package com.explik.diybirdyapp.graph.vertex;

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
}

package com.explik.diybirdyapp.graph.vertex;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.Property;

public abstract class TextContentVertex extends AbstractVertexFrame {
    @Property("value")
    public abstract void getValue();

    @Property("value")
    public abstract void setValue();

    @Adjacency(label = "hasLanguage")
    public abstract LanguageVertex getLanguage();

    @Adjacency(label = "hasLanguage")
    public abstract void setLanguage(LanguageVertex vertex);
}

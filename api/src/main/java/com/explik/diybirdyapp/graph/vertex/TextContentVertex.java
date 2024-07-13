package com.explik.diybirdyapp.graph.vertex;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.Property;

public abstract class TextContentVertex extends AbstractVertexFrame {
    @Property("value")
    public abstract String getValue();

    @Property("value")
    public abstract void setValue(String value);

    public LanguageVertex getLanguage() {
        return traverse(g -> g.out("hasLanguage")).nextExplicit(LanguageVertex.class);
    }

    public void setLanguage(LanguageVertex vertex) {
        addFramedEdgeExplicit("hasLanguage", vertex);
    }
}

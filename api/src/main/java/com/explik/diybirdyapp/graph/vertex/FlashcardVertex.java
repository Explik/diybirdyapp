package com.explik.diybirdyapp.graph.vertex;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Adjacency;
import org.apache.tinkerpop.gremlin.structure.Direction;

public abstract class FlashcardVertex extends AbstractVertexFrame {
    public TextContentVertex getLeftContent() {
        return traverse(g -> g.out("hasLeftContent")).nextExplicit(TextContentVertex.class);
    }

    public void setLeftContent(TextContentVertex vertex) {
        addFramedEdgeExplicit("hasLeftContent", vertex);
    }

    public TextContentVertex getRightContent() {
        return traverse(g -> g.out("hasRightContent")).nextExplicit(TextContentVertex.class);
    }

    public void setRightContent(TextContentVertex vertex) {
        addFramedEdgeExplicit("hasRightContent", vertex);
    }
}

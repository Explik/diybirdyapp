package com.explik.diybirdyapp.graph.vertex;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Adjacency;
import org.apache.tinkerpop.gremlin.structure.Direction;

public abstract class FlashcardVertex extends AbstractVertexFrame {
    public TextContentVertex getLeftContent() {
        return traverse(g -> g.out("hasLeftContent")).nextOrDefaultExplicit(TextContentVertex.class, null);
    }

    public void setLeftContent(TextContentVertex vertex) {
        var existingContent = getLeftContent();

        if (existingContent != null)
            existingContent.remove();

        addFramedEdgeExplicit("hasLeftContent", vertex);
    }

    public TextContentVertex getRightContent() {
        return traverse(g -> g.out("hasRightContent")).nextOrDefaultExplicit(TextContentVertex.class, null);
    }

    public void setRightContent(TextContentVertex vertex) {
        var existingContent = getRightContent();

        if (existingContent != null)
            existingContent.remove();

        addFramedEdgeExplicit("hasRightContent", vertex);
    }
}

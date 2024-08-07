package com.explik.diybirdyapp.graph.vertex;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Property;

import java.util.List;

public abstract class FlashcardSetVertex extends AbstractVertexFrame {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    @Property("name")
    public abstract String getName();

    @Property("name")
    public abstract void setName(String name);

    @Property("description")
    public abstract String getDescription();

    @Property("description")
    public abstract void setDescription(String description);

    public void addFlashcard(FlashcardVertex flashcardVertex) {
        addFramedEdgeExplicit("hasFlashcard", flashcardVertex);
    }

    public List<? extends FlashcardVertex> getFlashcards() {
        return traverse(g -> g.outE("hasFlashcard").order().by("order").inV()).toListExplicit(FlashcardVertex.class);
    }
}

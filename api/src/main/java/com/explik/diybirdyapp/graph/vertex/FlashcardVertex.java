package com.explik.diybirdyapp.graph.vertex;

import com.syncleus.ferma.annotations.Adjacency;

public abstract class FlashcardVertex {
    @Adjacency(label = "hasLeftContent")
    public abstract TextContentVertex getLeftContent();

    @Adjacency(label = "hasLeftContent")
    public abstract void setLeftContent(TextContentVertex vertex);

    @Adjacency(label = "hasRightContent")
    public abstract TextContentVertex getRightContent();

    @Adjacency(label = "hasRightContent")
    public abstract void setRightContent(TextContentVertex vertex);
}

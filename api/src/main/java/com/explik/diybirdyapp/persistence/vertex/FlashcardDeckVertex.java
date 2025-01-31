package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class FlashcardDeckVertex extends ContentVertex {
    public FlashcardDeckVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "flashcardDeck";

    public static final String EDGE_FLASHCARD = "hasFlashcard";
    public static final String EDGE_FLASHCARD_PROPERTY_ORDER = "order";

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_DESCRIPTION = "description";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getName() {
        return getPropertyAsString(PROPERTY_NAME);
    }

    public void setName(String name) {
        setProperty(PROPERTY_NAME, name);
    }

    public String getDescription() {
        return getPropertyAsString(PROPERTY_DESCRIPTION);
    }

    public void setDescription(String description) {
        setProperty(PROPERTY_DESCRIPTION, description);
    }

    public void addFlashcard(FlashcardVertex flashcardVertex) {
        int maxOrder = this.traversalSource.V(this.vertex)
            .outE(EDGE_FLASHCARD)
            .values(EDGE_FLASHCARD_PROPERTY_ORDER)
            .tryNext()
            .map(order -> (int)order)
            .orElse(0);

        this.traversalSource.V(this.vertex)
                .addE(EDGE_FLASHCARD)
                .property(EDGE_FLASHCARD_PROPERTY_ORDER, maxOrder + 1)
                .to(flashcardVertex.vertex)
                .next();
        reload();
    }

    public void removeFlashcard(FlashcardVertex flashcardVertex) {
        removeEdge(EDGE_FLASHCARD, flashcardVertex);
    }

    public List<? extends FlashcardVertex> getFlashcards() {
        return VertexHelper.getOutgoingModels(this, EDGE_FLASHCARD, FlashcardVertex::new);
    }

    public static FlashcardDeckVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new FlashcardDeckVertex(traversalSource, vertex);
    }

    public static FlashcardDeckVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertexQuery = traversalSource.V().has(LABEL, PROPERTY_ID, id);
        if (!vertexQuery.hasNext())
            return null;
        return new FlashcardDeckVertex(traversalSource, vertexQuery.next());
    }
}

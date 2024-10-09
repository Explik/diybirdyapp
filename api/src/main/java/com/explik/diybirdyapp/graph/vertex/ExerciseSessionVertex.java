package com.explik.diybirdyapp.graph.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class ExerciseSessionVertex extends AbstractVertex {
    public ExerciseSessionVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseSession";

    public final static String EDGE_FLASHCARD_DECK = "hasFlashcardDeck";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_TYPE = "type";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getType() {
        return getPropertyAsString(PROPERTY_TYPE);
    }

    public void setType(String type) {
        setProperty(PROPERTY_TYPE, type);
    }

    public ExerciseVertex getCurrentExercise() {
        var vertexQuery = traversalSource.V(vertex).in(ExerciseVertex.EDGE_SESSION);
        if (!vertexQuery.hasNext())
            return null;
        return new ExerciseVertex(traversalSource, vertexQuery.next());
    }

    public List<ExerciseVertex> getExercises() {
        var exerciseVertices = traversalSource.V(vertex).in(ExerciseVertex.EDGE_SESSION).toList();
        return exerciseVertices.stream()
                .map(v -> new ExerciseVertex(traversalSource, v))
                .toList();
    }

    public void setFlashcardDeck(FlashcardDeckVertex flashcardDeckVertex) {
        addEdgeOneToOne(EDGE_FLASHCARD_DECK, flashcardDeckVertex);
    }

    public FlashcardDeckVertex getFlashcardDeck() {
        var vertexQuery = traversalSource.V(vertex).out(EDGE_FLASHCARD_DECK);
        if (!vertexQuery.hasNext())
            return null;
        return new FlashcardDeckVertex(traversalSource, vertexQuery.next());
    }

    public static ExerciseSessionVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id).next();
        return new ExerciseSessionVertex(traversalSource, vertex);
    }
}

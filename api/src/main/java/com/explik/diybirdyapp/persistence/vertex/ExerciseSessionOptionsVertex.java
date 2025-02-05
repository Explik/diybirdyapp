package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ExerciseSessionOptionsVertex extends AbstractVertex {
    public ExerciseSessionOptionsVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseSessionOptions";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_FLASHCARD_SIDE = "flashcardSide";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getFlashcardSide() {
        return getPropertyAsString(PROPERTY_FLASHCARD_SIDE, null);
    }

    public void setFlashcardSide(String flashcardSide) {
        setProperty(PROPERTY_FLASHCARD_SIDE, flashcardSide);
    }

    public static ExerciseSessionOptionsVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseSessionOptionsVertex(traversalSource, vertex);
    }
}

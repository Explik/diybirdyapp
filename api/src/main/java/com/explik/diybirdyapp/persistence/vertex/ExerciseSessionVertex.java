package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class ExerciseSessionVertex extends AbstractVertex {
    public ExerciseSessionVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseSession";

    public final static String EDGE_FLASHCARD_DECK = "hasFlashcardDeck";
    public final static String EDGE_OPTIONS = "hasOptions";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_TYPE = "type";
    public final static String PROPERTY_COMPLETED = "completed";

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

    public boolean getCompleted() {
        return getProperty(PROPERTY_COMPLETED, false);
    }

    public void setCompleted(boolean completed) {
        setProperty(PROPERTY_COMPLETED, completed);
    }

    public ExerciseVertex getCurrentExercise() {
        return VertexHelper.getOptionalIngoingModel(this, ExerciseVertex.EDGE_SESSION, ExerciseVertex::new);
    }

    public List<ExerciseVertex> getExercises() {
        return VertexHelper.getIngoingModels(this, ExerciseVertex.EDGE_SESSION, ExerciseVertex::new);
    }

    public void setFlashcardDeck(FlashcardDeckVertex flashcardDeckVertex) {
        addEdgeOneToOne(EDGE_FLASHCARD_DECK, flashcardDeckVertex);
    }

    public FlashcardDeckVertex getFlashcardDeck() {
        return VertexHelper.getOptionalOutgoingModel(this, EDGE_FLASHCARD_DECK, FlashcardDeckVertex::new);
    }

    public void setOptions(ExerciseSessionOptionsVertex optionsVertex) {
        addEdgeOneToOne(EDGE_OPTIONS, optionsVertex);
    }

    public ExerciseSessionOptionsVertex getOptions() {
        return VertexHelper.getOptionalOutgoingModel(this, EDGE_OPTIONS, ExerciseSessionOptionsVertex::new);
    }

    public static ExerciseSessionVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id).next();
        return new ExerciseSessionVertex(traversalSource, vertex);
    }

    public static ExerciseSessionVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseSessionVertex(traversalSource, vertex);
    }
}

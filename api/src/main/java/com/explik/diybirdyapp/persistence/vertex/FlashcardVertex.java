package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.stream.Collectors;

public class FlashcardVertex extends ContentVertex {
    public FlashcardVertex(AbstractVertex vertex) {
        super(vertex.getUnderlyingSource(), vertex.getUnderlyingVertex());
    }

    public FlashcardVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "flashcard";

    public final static String EDGE_LEFT_CONTENT = "hasLeftContent";
    public final static String EDGE_RIGHT_CONTENT = "hasRightContent";

    public final static String PROPERTY_ID = "id";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    @Override
    public void makeStatic() {
        super.makeStatic();

        getLeftContent().makeStatic();
        getRightContent().makeStatic();
    }

    public FlashcardDeckVertex getDeck() {
        return VertexHelper.getIngoingModel(this, FlashcardDeckVertex.EDGE_FLASHCARD, FlashcardDeckVertex::new);
    }

    public ContentVertex getLeftContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_LEFT_CONTENT, VertexHelper::createContent);
    }

    public void setLeftContent(ContentVertex vertex) {
        addEdgeOneToOne(EDGE_LEFT_CONTENT, vertex);
    }

    public ContentVertex getRightContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_RIGHT_CONTENT, VertexHelper::createContent);
    }

    public void setRightContent(ContentVertex vertex) {
        addEdgeOneToOne(EDGE_RIGHT_CONTENT, vertex);
    }

    public static FlashcardVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new FlashcardVertex(traversalSource, vertex);
    }

    public static List<FlashcardVertex> findAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel(LABEL).toList();

        return vertices.stream()
                .map(v -> new FlashcardVertex(traversalSource, v))
                .collect(Collectors.toList());
    }

    public static FlashcardVertex findById(GraphTraversalSource traversalSource, String id) {
        var query = traversalSource.V().has(LABEL, PROPERTY_ID, id);

        if (!query.hasNext())
            throw new IllegalArgumentException("Flashcard with id " + id + " not found");

        return new FlashcardVertex(traversalSource, query.next());
    }

    public static List<FlashcardVertex> findByDeckId(GraphTraversalSource traversalSource, String deckId) {
        var vertices = traversalSource.V()
                .has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, deckId).out(FlashcardDeckVertex.EDGE_FLASHCARD)
                .toList();

        return vertices.stream()
                .map(v -> new FlashcardVertex(traversalSource, v))
                .collect(Collectors.toList());
    }

    public static FlashcardVertex findFirstNonExercised(GraphTraversalSource traversalSource, String sessionId, String exerciseType) {
        var query = traversalSource.V()
                .has(ExerciseSessionVertex.LABEL, ExerciseSessionVertex.PROPERTY_ID, sessionId)
                .out(ExerciseSessionVertex.EDGE_FLASHCARD_DECK)
                .out(FlashcardDeckVertex.EDGE_FLASHCARD)
                .not(__.in(ExerciseVertex.EDGE_CONTENT)
                        .has(ExerciseVertex.PROPERTY_TYPE, exerciseType)
                        .out(ExerciseVertex.EDGE_SESSION)
                        .has(ExerciseSessionVertex.LABEL, ExerciseSessionVertex.PROPERTY_ID, sessionId));

        if (!query.hasNext())
            return null;

        return new FlashcardVertex(traversalSource, query.next());
    }
}

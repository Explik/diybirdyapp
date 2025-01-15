package com.explik.diybirdyapp.persistence.vertex;

import com.explik.diybirdyapp.model.ExerciseContentFlashcardModel;
import com.explik.diybirdyapp.model.FlashcardModel;
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
        var deckVertex = traversalSource.V(vertex).in(FlashcardDeckVertex.EDGE_FLASHCARD).next();
        return new FlashcardDeckVertex(traversalSource, deckVertex);
    }

    public ContentVertex getLeftContent() {
        var leftContentVertex = traversalSource.V(vertex).out(EDGE_LEFT_CONTENT).next();
        return createContent(traversalSource, leftContentVertex);
    }

    public void setLeftContent(ContentVertex vertex) {
        addEdgeOneToOne(EDGE_LEFT_CONTENT, vertex);
    }

    public ContentVertex getRightContent() {
        var rightContentVertex = traversalSource.V(vertex).out(EDGE_RIGHT_CONTENT).next();
        return createContent(traversalSource, rightContentVertex);
    }

    public void setRightContent(ContentVertex vertex) {
        addEdgeOneToOne(EDGE_RIGHT_CONTENT, vertex);
    }

    public static FlashcardVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new FlashcardVertex(traversalSource, vertex);
    }

    protected static ContentVertex createContent(GraphTraversalSource traversalSource, Vertex vertex) {
        if (vertex.label().equals(TextContentVertex.LABEL))
            return new TextContentVertex(traversalSource, vertex);
        else if(vertex.label().equals(ImageContentVertex.LABEL))
            return new ImageContentVertex(traversalSource, vertex);

        throw new RuntimeException("Unknown content type: " + vertex.label());
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

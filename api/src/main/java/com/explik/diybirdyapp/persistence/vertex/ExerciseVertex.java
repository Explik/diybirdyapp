package com.explik.diybirdyapp.persistence.vertex;

import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class ExerciseVertex extends AbstractVertex {
    public ExerciseVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exercise";

    public final static String EDGE_CONTENT = "hasContent";
    public final static String EDGE_CONTENT_PROPERTY_FLASHCARD_SIDE = "flashcardSide";
    public final static String EDGE_SESSION = "hasSession";
    public final static String EDGE_OPTION = "hasOption";
    public final static String EDGE_OPTION_PAIR = "hasOptionPair";
    public final static String EDGE_OPTION_PROPERTY_ORDER = "order";
    public final static String EDGE_CORRECT_OPTION = "hasCorrectOption";
    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_TARGET_LANGUAGE = "targetLanguage";

    public final static String PROPERTY_TYPE = "exerciseType";

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

    public ContentVertex getContent() {
        var contentVertex = traversalSource.V(vertex).out(EDGE_CONTENT).next();
        return VertexHelper.createContent(traversalSource, contentVertex);
    }

    public TextContentVertex getTextContent() {
        return (TextContentVertex) getContent();
    }

    public FlashcardVertex getFlashcardContent() {
        return (FlashcardVertex) getContent();
    }

    public void setContent(AbstractVertex outVertex) {
        addEdgeOneToOne(EDGE_CONTENT, outVertex);
    }

    public void setFlashcardContent(FlashcardVertex flashcardVertex, String flashcardSide) {
        this.traversalSource.V(this.vertex)
                .addE(EDGE_CONTENT)
                .property(EDGE_CONTENT_PROPERTY_FLASHCARD_SIDE, flashcardSide)
                .to(flashcardVertex.vertex)
                .next();
        reload();
    }

    public String getFlashcardSide() {
        return VertexHelper.getOptionalOutgoingProperty(this, EDGE_CONTENT, EDGE_CONTENT_PROPERTY_FLASHCARD_SIDE);
    }

    public String getTargetLanguage() {
        return getPropertyAsString(PROPERTY_TARGET_LANGUAGE);
    }

    public void setTargetLanguage(String targetLanguage) {
        setProperty(PROPERTY_TARGET_LANGUAGE, targetLanguage);
    }

    public void addOption(AbstractVertex option) {
        addOrderedEdgeOneToMany(EDGE_OPTION, option, EDGE_OPTION_PROPERTY_ORDER, 0);
    }

    public void removeOption(AbstractVertex option) {
        removeEdge(EDGE_OPTION, option);
    }

    public List<? extends ContentVertex> getOptions() {
        return VertexHelper.getOrderedOutgoingModels(
                this,
                EDGE_OPTION,
                EDGE_OPTION_PROPERTY_ORDER,
                VertexHelper::createContent);
    }

    public void addCorrectOption(AbstractVertex option) {
        addEdgeOneToMany(EDGE_CORRECT_OPTION, option);
    }

    public void removeCorrectOption(AbstractVertex option) {
        removeEdge(EDGE_CORRECT_OPTION, option);
    }

    public List<? extends ContentVertex> getCorrectOptions() {
        return VertexHelper.getOutgoingModels(
                this,
                EDGE_CORRECT_OPTION,
                VertexHelper::createContent);
    }

    public void addOptionPair(AbstractVertex optionPair) {
        addEdgeOneToMany(EDGE_OPTION_PAIR, optionPair);
    }

    public void removeOptionPair(AbstractVertex optionPair) {
        removeEdge(EDGE_OPTION_PAIR, optionPair);
    }

    public List<? extends PairVertex> getOptionPairs() {
        return VertexHelper.getOutgoingModels(
                this,
                EDGE_OPTION_PAIR,
                PairVertex::new);
    }

    public ExerciseSessionVertex getSession() {
        return VertexHelper.getOptionalOutgoingModel(this, EDGE_SESSION, ExerciseSessionVertex::new);
    }

    public void setSession(AbstractVertex session) {
        if (session == null)
            return;
        addEdgeOneToMany(EDGE_SESSION, session);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExerciseVertex other)
            return this.getId().equals(other.getId());

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    public static ExerciseVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseVertex(traversalSource, vertex);
    }

    public static ExerciseVertex getById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().has(LABEL, PROPERTY_ID, id).next();
        return new ExerciseVertex(traversalSource, vertex);
    }

    public static List<ExerciseVertex> getAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel(LABEL).toList();
        return vertices.stream()
                .map(v -> new ExerciseVertex(traversalSource, v))
                .toList();
    }
}

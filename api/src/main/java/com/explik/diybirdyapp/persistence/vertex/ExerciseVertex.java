package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.List;

public class ExerciseVertex extends AbstractVertex {
    public ExerciseVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exercise";

    public final static String EDGE_TYPE = "hasType";
    public final static String EDGE_CONTENT = "hasContent";
    public final static String EDGE_CONTENT_PROPERTY_FLASHCARD_SIDE = "flashcardSide";
    public final static String EDGE_IS_BASED_ON = "isBasedOn";
    public final static String EDGE_OPTION = "hasOption";
    public final static String EDGE_OPTION_PAIR = "hasOptionPair";
    public final static String EDGE_OPTION_PROPERTY_ORDER = "order";
    public final static String EDGE_CORRECT_OPTION = "hasCorrectOption";
    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_TARGET_LANGUAGE = "targetLanguage";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public ExerciseTypeVertex getExerciseType() {
        var exerciseTypeVertex = traversalSource.V(vertex).out(EDGE_TYPE).next();
        return new ExerciseTypeVertex(traversalSource, exerciseTypeVertex);
    }

    public void setExerciseType(ExerciseTypeVertex exerciseTypeVertex) {
        addEdgeOneToOne(EDGE_TYPE, exerciseTypeVertex);
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

    public AbstractVertex getBasedOnContent() {
        var vertices = traversalSource.V(vertex).out(EDGE_IS_BASED_ON).toList();
        if (vertices.isEmpty()) {
            return null;
        }
        return new AbstractVertex(traversalSource, vertices.get(0));
    }

    public void setBasedOnContent(AbstractVertex outVertex) {
        addEdgeOneToOne(EDGE_IS_BASED_ON, outVertex);
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

    // =========================================================
    // Multi-stage tap-pairs exercise server-side state
    // =========================================================

    private static final String PROP_MS_ANSWERED_COUNT  = "msAnsweredCount";
    private static final String PROP_MS_MATCHED_IDS     = "msMatchedLeftIds";
    private static final String PROP_MS_LEFT_IDS        = "msCurrentLeftIds";
    private static final String PROP_MS_RIGHT_IDS       = "msCurrentRightIds";

    public int getMsAnsweredCount() {
        return getProperty(PROP_MS_ANSWERED_COUNT, 0);
    }

    public void setMsAnsweredCount(int count) {
        setProperty(PROP_MS_ANSWERED_COUNT, count);
    }

    public List<String> getMsMatchedLeftIds() {
        String raw = getProperty(PROP_MS_MATCHED_IDS, "");
        return raw.isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(raw.split(",")));
    }

    public void setMsMatchedLeftIds(List<String> ids) {
        setProperty(PROP_MS_MATCHED_IDS, (ids == null || ids.isEmpty()) ? "" : String.join(",", ids));
    }

    public List<String> getMsCurrentLeftOptionIds() {
        String raw = getProperty(PROP_MS_LEFT_IDS, "");
        return raw.isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(raw.split(",")));
    }

    public void setMsCurrentLeftOptionIds(List<String> ids) {
        setProperty(PROP_MS_LEFT_IDS, (ids == null || ids.isEmpty()) ? "" : String.join(",", ids));
    }

    public List<String> getMsCurrentRightOptionIds() {
        String raw = getProperty(PROP_MS_RIGHT_IDS, "");
        return raw.isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(raw.split(",")));
    }

    public void setMsCurrentRightOptionIds(List<String> ids) {
        setProperty(PROP_MS_RIGHT_IDS, (ids == null || ids.isEmpty()) ? "" : String.join(",", ids));
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

package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class ExerciseSessionStateVertex extends AbstractVertex {
    public ExerciseSessionStateVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseSessionState";

    public final static String PROPERTY_TYPE = "type";
    public final static String PROPERTY_CURRENT_CONTENT_INDEX = "currentContentIndex";
    public final static String PROPERTY_CURRENT_ROUND = "currentRound";
    private static final String PROPERTY_HAS_SEEN_VIEW_EXERCISE_PREFIX = "hasSeenViewExercise_";
    
    // Exercise round configuration
    public static final int MAX_EXERCISES_PER_CONTENT = 5; // Number of rounds per content piece

    public final static String EDGE_CONTENT = "hasContent";
    public final static String EDGE_ACTIVE_CONTENT = "hasActiveContent";
    public final static String EDGE_ACTIVE_CONTENT_ORDER = "order";
    public final static String EDGE_PRACTICED_CONTENT = "hasPracticedContent";

    public String getType() {
        return getPropertyAsString(PROPERTY_TYPE);
    }

    public void setType(String type) {
        setProperty(PROPERTY_TYPE, type);
    }

    public <T> T getPropertyValue(String propertyName) {
        return getProperty(propertyName);
    }

    public <T> T getPropertyValue(String propertyName, T defaultValue) {
        return getProperty(propertyName, defaultValue);
    }

    public <T> void setPropertyValue(String propertyName, T value) {
        if (propertyName.equals(PROPERTY_TYPE))
            throw new IllegalArgumentException("Use setType() to set the type property.");

        setProperty(propertyName, value);
    }

    public ContentVertex getContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_CONTENT, VertexHelper::createContent);
    }

    public void setContent(ContentVertex contentVertex) {
        addEdgeOneToOne(EDGE_CONTENT, contentVertex);
    }
    
    public ExerciseSessionVertex getSession() {
        return VertexHelper.getIngoingModel(this, ExerciseSessionVertex.EDGE_STATE, ExerciseSessionVertex::new);
    }
    
    public List<AbstractVertex> getActiveContent() {
        return VertexHelper.getOrderedOutgoingModels(this, EDGE_ACTIVE_CONTENT, EDGE_ACTIVE_CONTENT_ORDER, (source, vertex) -> {
            String label = vertex.label();
            
            // Handle PronunciationVertex (not a ContentVertex subclass)
            if (label.equals(PronunciationVertex.LABEL)) {
                return new PronunciationVertex(source, vertex);
            }
            
            // Handle all ContentVertex types (including FlashcardVertex, TextContentVertex, AudioContentVertex, etc.)
            // VertexHelper.createContent will instantiate the correct concrete type based on the label
            return VertexHelper.createContent(source, vertex);
        });
    }
    
    public void addActiveContent(AbstractVertex vertex) {
        addOrderedEdgeOneToMany(
                EDGE_ACTIVE_CONTENT,
                vertex,
                EDGE_ACTIVE_CONTENT_ORDER,
                getNextOrderedEdgeValue(EDGE_ACTIVE_CONTENT, EDGE_ACTIVE_CONTENT_ORDER));
    }
    
    public void clearActiveContent() {
        removeEdges(EDGE_ACTIVE_CONTENT);
    }
    
    /**
     * Gets the current index in the active content list being processed.
     * @return The current content index, or 0 if not set
     */
    public int getCurrentContentIndex() {
        return getProperty(PROPERTY_CURRENT_CONTENT_INDEX, 0);
    }
    
    /**
     * Sets the current index in the active content list being processed.
     * @param index The content index to set
     */
    public void setCurrentContentIndex(int index) {
        setProperty(PROPERTY_CURRENT_CONTENT_INDEX, index);
    }
    
    /**
     * Gets the current round number (0-based).
     * @return The current round number, or 0 if not set
     */
    public int getCurrentRound() {
        return getProperty(PROPERTY_CURRENT_ROUND, 0);
    }
    
    /**
     * Sets the current round number.
     * @param round The round number to set
     */
    public void setCurrentRound(int round) {
        setProperty(PROPERTY_CURRENT_ROUND, round);
    }
    
    /**
     * Gets the exercise count for a specific content vertex.
     * Stored as a property like "exerciseCount_<vertexId>".
     * @param vertexId The ID of the content vertex
     * @return The exercise count, or 0 if not set
     */
    public int getExerciseCountForContent(String vertexId) {
        return getProperty("exerciseCount_" + vertexId, 0);
    }
    
    /**
     * Increments the exercise count for a specific content vertex.
     * @param vertexId The ID of the content vertex
     */
    public void incrementExerciseCountForContent(String vertexId) {
        int currentCount = getExerciseCountForContent(vertexId);
        setProperty("exerciseCount_" + vertexId, currentCount + 1);
    }
    
    /**
     * Checks if a content vertex has been exercised the maximum number of times.
     * @param vertexId The ID of the content vertex
     * @return true if the content has been exercised MAX_EXERCISES_PER_CONTENT or more times
     */
    public boolean hasReachedMaxExercises(String vertexId) {
        return getExerciseCountForContent(vertexId) >= MAX_EXERCISES_PER_CONTENT;
    }
    
    /**
     * Gets the last exercise type used for a specific content vertex.
     * Stored as a property like "lastExerciseType_<vertexId>".
     * @param vertexId The ID of the content vertex
     * @return The last exercise type ID, or null if not set
     */
    public String getLastExerciseTypeForContent(String vertexId) {
        return getProperty("lastExerciseType_" + vertexId, null);
    }
    
    /**
     * Sets the last exercise type used for a specific content vertex.
     * @param vertexId The ID of the content vertex
     * @param exerciseType The exercise type ID that was just used
     */
    public void setLastExerciseTypeForContent(String vertexId, String exerciseType) {
        setProperty("lastExerciseType_" + vertexId, exerciseType);
    }

    public boolean hasSeenViewExerciseForContent(String vertexId) {
        return getProperty(PROPERTY_HAS_SEEN_VIEW_EXERCISE_PREFIX + vertexId, false);
    }

    public void setHasSeenViewExerciseForContent(String vertexId, boolean hasSeen) {
        setProperty(PROPERTY_HAS_SEEN_VIEW_EXERCISE_PREFIX + vertexId, hasSeen);
    }

    public List<FlashcardVertex> getPracticedContent() {
        return VertexHelper.getOutgoingModels(this, EDGE_PRACTICED_CONTENT, FlashcardVertex::new);
    }

    public void addPracticedContent(AbstractVertex vertex) {
        addEdgeOneToMany(EDGE_PRACTICED_CONTENT, vertex);
    }

    public static ExerciseSessionStateVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseSessionStateVertex(traversalSource, vertex);
    }

    private long getNextOrderedEdgeValue(String edgeLabel, String orderProperty) {
        long nextValue = 0;

        for (Object existingValue : traversalSource.V(vertex).outE(edgeLabel).values(orderProperty).toList()) {
            if (existingValue instanceof Number numericValue) {
                nextValue = Math.max(nextValue, numericValue.longValue() + 1);
            }
        }

        return nextValue;
    }

    public static ExerciseSessionStateVertex findBy(GraphTraversalSource traversalSource, String type, ContentVertex contentVertex, ExerciseSessionVertex sessionVertex) {
        var vertex = traversalSource.V()
                .hasLabel(LABEL)
                .has(PROPERTY_TYPE, type)
                .where(__.outE(EDGE_CONTENT).inV().is(contentVertex.getUnderlyingVertex()))
                .where(__.inE(ExerciseSessionVertex.EDGE_STATE).outV().is(sessionVertex.getUnderlyingVertex()))
                .tryNext();

        return vertex.map(v -> new ExerciseSessionStateVertex(traversalSource, v)).orElse(null);
    }
}

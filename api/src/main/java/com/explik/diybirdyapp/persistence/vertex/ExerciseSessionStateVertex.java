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

    public final static String EDGE_CONTENT = "hasContent";
    public final static String EDGE_ACTIVE_CONTENT = "hasActiveContent";

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
    
    public List<AbstractVertex> getActiveContent() {
        return VertexHelper.getOutgoingModels(this, EDGE_ACTIVE_CONTENT, (source, vertex) -> {
            String label = vertex.label();
            if (label.equals(PronunciationVertex.LABEL)) {
                return new PronunciationVertex(source, vertex);
            }
            return VertexHelper.createContent(source, vertex);
        });
    }
    
    public void addActiveContent(AbstractVertex vertex) {
        addEdgeOneToMany(EDGE_ACTIVE_CONTENT, vertex);
    }
    
    public void clearActiveContent() {
        removeEdges(EDGE_ACTIVE_CONTENT);
    }

    public static ExerciseSessionStateVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseSessionStateVertex(traversalSource, vertex);
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

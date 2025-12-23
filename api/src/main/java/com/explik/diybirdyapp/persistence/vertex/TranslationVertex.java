package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class TranslationVertex extends AbstractVertex {
    public TranslationVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "translation";

    public static final String PROPERTY_ID = "id";

    public static final String EDGE_HAS_SOURCE_CONTENT = "hasSourceContent";
    public static final String EDGE_HAS_TARGET_CONTENT = "hasTargetContent";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public ContentVertex getSourceContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_HAS_SOURCE_CONTENT, VertexHelper::createContent);
    }

    public void setSourceContent(ContentVertex sourceContent) {
        addEdgeOneToOne(EDGE_HAS_SOURCE_CONTENT, sourceContent);
    }

    public AbstractVertex getTargetContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_HAS_TARGET_CONTENT, VertexHelper::createContent);
    }

    public void setTargetContent(ContentVertex targetContent) {
        addEdgeOneToOne(EDGE_HAS_TARGET_CONTENT, targetContent);
    }

    public static TranslationVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new TranslationVertex(traversalSource, vertex);
    }

    public static TranslationVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertexQuery = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id);
        if (!vertexQuery.hasNext())
            return null;
        return new TranslationVertex(traversalSource, vertexQuery.next());
    }

    public static List<TranslationVertex> findAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel(LABEL).toList();

        return vertices.stream()
                .map(v -> new TranslationVertex(traversalSource, v))
                .toList();
    }
}

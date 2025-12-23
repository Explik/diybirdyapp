package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class TranscriptionVertex extends AbstractVertex {
    public TranscriptionVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "transcription";

    public static final String PROPERTY_ID = "id";

    public static final String EDGE_HAS_SOURCE_CONTENT = "hasSourceContent";
    public static final String EDGE_HAS_TEXT_CONTENT = "hasTextContent";
    public static final String EDGE_HAS_SYSTEM = "hasSystem";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public void setSourceContent(AbstractVertex sourceContent) {
        addEdgeOneToOne(EDGE_HAS_SOURCE_CONTENT, sourceContent);
    }

    public AbstractVertex getSourceContent() {
        var vertexQuery = traversalSource.V(vertex).out(EDGE_HAS_SOURCE_CONTENT);
        if (!vertexQuery.hasNext())
            return null;
        return new AbstractVertex(traversalSource, vertexQuery.next());
    }

    public void setTextContent(TextContentVertex textContent) {
        addEdgeOneToOne(EDGE_HAS_TEXT_CONTENT, textContent);
    }

    public TextContentVertex getTextContent() {
        var vertexQuery = traversalSource.V(vertex).out(EDGE_HAS_TEXT_CONTENT);
        if (!vertexQuery.hasNext())
            return null;
        return new TextContentVertex(traversalSource, vertexQuery.next());
    }

    public void setSystem(TranscriptionSystemVertex system) {
        addEdgeOneToOne(EDGE_HAS_SYSTEM, system);
    }

    public TranscriptionSystemVertex getSystem() {
        var vertexQuery = traversalSource.V(vertex).out(EDGE_HAS_SYSTEM);
        if (!vertexQuery.hasNext())
            return null;
        return new TranscriptionSystemVertex(traversalSource, vertexQuery.next());
    }

    public static TranscriptionVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new TranscriptionVertex(traversalSource, vertex);
    }

    public static TranscriptionVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertexQuery = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id);
        if (!vertexQuery.hasNext())
            return null;
        return new TranscriptionVertex(traversalSource, vertexQuery.next());
    }

    public static List<TranscriptionVertex> findAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel(LABEL).toList();

        return vertices.stream()
                .map(v -> new TranscriptionVertex(traversalSource, v))
                .toList();
    }
}

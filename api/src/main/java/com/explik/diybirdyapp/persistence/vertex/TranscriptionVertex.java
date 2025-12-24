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

    public static final String EDGE_SOURCE_CONTENT = "hasSourceContent";
    public static final String EDGE_TEXT_CONTENT = "hasTextContent";
    public static final String EDGE_SYSTEM = "hasSystem";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public void setSourceContent(ContentVertex sourceContent) {
        addEdgeOneToOne(EDGE_SOURCE_CONTENT, sourceContent);
    }

    public ContentVertex getSourceContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_SOURCE_CONTENT, VertexHelper::createContent);
    }

    public void setTextContent(TextContentVertex textContent) {
        addEdgeOneToOne(EDGE_TEXT_CONTENT, textContent);
    }

    public TextContentVertex getTextContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_TEXT_CONTENT, TextContentVertex::new);
    }

    public void setSystem(TranscriptionSystemVertex system) {
        addEdgeOneToOne(EDGE_SYSTEM, system);
    }

    public TranscriptionSystemVertex getSystem() {
        return VertexHelper.getOutgoingModel(this, EDGE_SYSTEM, TranscriptionSystemVertex::new);
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

    public static TranscriptionVertex findBySourceContentId(GraphTraversalSource traversalSource, String contentId) {
        var vertexQuery = traversalSource.V()
                .hasLabel(LABEL)
                .out(EDGE_SOURCE_CONTENT)
                .has(ContentVertex.PROPERTY_ID, contentId)
                .inE(EDGE_SOURCE_CONTENT)
                .outV();
        
        return vertexQuery.hasNext() ? new TranscriptionVertex(traversalSource, vertexQuery.next()) : null;
    }
}

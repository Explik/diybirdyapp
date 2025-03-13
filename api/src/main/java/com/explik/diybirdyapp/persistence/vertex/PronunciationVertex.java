package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.annotation.Nullable;

public class PronunciationVertex extends AbstractVertex {
    public PronunciationVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "pronunciation";
    public static final String PROPERTY_ID = "id";
    public static final String EDGE_AUDIO_CONTENT = "hasAudioContent";
    public static final String EDGE_TEXT_CONTENT = "hasTextContent";

    public String getId() {
        return getProperty(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public @Nullable AudioContentVertex getAudioContent() {
        return VertexHelper.getOptionalOutgoingModel(this, EDGE_AUDIO_CONTENT, AudioContentVertex::new);
    }

    public void setAudioContent(AbstractVertex audioContentVertex) {
        addEdgeOneToOne(EDGE_AUDIO_CONTENT, audioContentVertex);
    }

    public @Nullable TextContentVertex getTextContent() {
        return VertexHelper.getOptionalOutgoingModel(this, EDGE_TEXT_CONTENT, TextContentVertex::new);
    }

    public void setTextContent(AbstractVertex textContentVertex) {
        addEdgeOneToOne(EDGE_TEXT_CONTENT, textContentVertex);
    }

    public static PronunciationVertex create(GraphTraversalSource traversalSource) {
        var graphVertex = traversalSource.addV(LABEL).next();
        return new PronunciationVertex(traversalSource, graphVertex);
    }
}

package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class PronunciationVertex extends AbstractVertex {
    public PronunciationVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "pronunciation";
    public static final String PROPERTY_ID = "id";
    public static final String EDGE_AUDIO_CONTENT = "hasAudioContent";

    public String getId() {
        return getProperty(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public AudioContentVertex getAudioContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_AUDIO_CONTENT, AudioContentVertex::new);
    }

    public void setAudioContent(AbstractVertex audioContentVertex) {
        addEdgeOneToOne(EDGE_AUDIO_CONTENT, audioContentVertex);
    }

    public static PronunciationVertex create(GraphTraversalSource traversalSource) {
        var graphVertex = traversalSource.addV(LABEL).next();
        return new PronunciationVertex(traversalSource, graphVertex);
    }
}

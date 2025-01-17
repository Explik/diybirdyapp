package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class AudioContentVertex extends ContentVertex {
    public AudioContentVertex(AbstractVertex vertex) {
        super(vertex.getUnderlyingSource(), vertex.getUnderlyingVertex());
    }

    public AudioContentVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "audioContent";
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_URL = "url";
    public static final String EDGE_LANGUAGE = "hasLanguage";

    public String getId() {
        return getProperty(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getUrl() {
        return getProperty(PROPERTY_URL);
    }

    public void setUrl(String audioUrl) {
        setProperty(PROPERTY_URL, audioUrl);
    }

    public LanguageVertex getLanguage() {
        return VertexHelper.getOutgoingModel(this, EDGE_LANGUAGE, LanguageVertex::new);
    }

    public void setLanguage(AbstractVertex languageVertex) {
        addEdgeOneToOne(EDGE_LANGUAGE, languageVertex);
    }

    public static AudioContentVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new AudioContentVertex(traversalSource, vertex);
    }

    public static AudioContentVertex getById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().has(ContentVertex.PROPERTY_ID, id).next();
        return new AudioContentVertex(traversalSource, vertex);
    }
}

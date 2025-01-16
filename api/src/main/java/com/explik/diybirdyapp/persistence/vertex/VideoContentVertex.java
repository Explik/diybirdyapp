package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class VideoContentVertex extends ContentVertex {
    public VideoContentVertex(AbstractVertex vertex) {
        super(vertex.getUnderlyingSource(), vertex.getUnderlyingVertex());
    }

    public VideoContentVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "videoContent";
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

    public void setUrl(String videoUrl) {
        setProperty(PROPERTY_URL, videoUrl);
    }

    public LanguageVertex getLanguage() {
        var languageVertex = traversalSource.V(vertex).out(EDGE_LANGUAGE).next();
        return new LanguageVertex(traversalSource, languageVertex);
    }

    public void setLanguage(AbstractVertex languageVertex) {
        addEdgeOneToOne(EDGE_LANGUAGE, languageVertex);
    }

    public static VideoContentVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new VideoContentVertex(traversalSource, vertex);
    }

    public static VideoContentVertex getById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().has(ContentVertex.PROPERTY_ID, id).next();
        return new VideoContentVertex(traversalSource, vertex);
    }
}

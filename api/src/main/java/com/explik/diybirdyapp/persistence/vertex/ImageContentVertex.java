package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ImageContentVertex extends ContentVertex {
    public ImageContentVertex(AbstractVertex vertex) {
        super(vertex.getUnderlyingSource(), vertex.getUnderlyingVertex());
    }

    public ImageContentVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "imageContent";
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_URL = "url";

    public String getId() {
        return getProperty(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getUrl() {
        return getProperty(PROPERTY_URL);
    }

    public void setUrl(String imageUrl) {
        setProperty(PROPERTY_URL, imageUrl);
    }

    public static ImageContentVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ImageContentVertex(traversalSource, vertex);
    }

    public static ImageContentVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().has(ContentVertex.PROPERTY_ID, id).tryNext().orElse(null);
        return vertex != null ? new ImageContentVertex(traversalSource, vertex) : null;
    }
}

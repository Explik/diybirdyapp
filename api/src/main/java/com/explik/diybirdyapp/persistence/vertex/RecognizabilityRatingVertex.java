package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class RecognizabilityRatingVertex extends AbstractVertex {
    public RecognizabilityRatingVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "recognizabilityRating";

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_RATING = "rating";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getRating() {
        return getPropertyAsString(PROPERTY_RATING);
    }

    public void setRating(String rating) {
        setProperty(PROPERTY_RATING, rating);
    }

    public static RecognizabilityRatingVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new RecognizabilityRatingVertex(traversalSource, vertex);
    }

    public static RecognizabilityRatingVertex findById(GraphTraversalSource traversalSource, String ratingId) {
        var vertex = traversalSource.V().has(PROPERTY_ID, ratingId).tryNext().orElse(null);
        return vertex != null ? new RecognizabilityRatingVertex(traversalSource, vertex) : null;
    }
}

package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ContentVertex extends AbstractVertex {
    public ContentVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_STATIC = "isStatic";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public boolean isStatic() {
        return getPropertyAsBoolean(PROPERTY_STATIC, false);
    }

    // Irreversible operation, once content is static it stays static
    // TIP: call this method last as it will hinder any further updates
    public void makeStatic() {
        if (isStatic())
            return;

        setProperty(PROPERTY_STATIC, true);
    }

    @Override
    protected <T> void setProperty(String propertyKey, T value) {
        if (isStatic())
            throw new IllegalStateException("Cannot modify static content");

        super.setProperty(propertyKey, value);
    }


}

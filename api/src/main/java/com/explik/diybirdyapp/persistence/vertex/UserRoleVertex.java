package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class UserRoleVertex extends AbstractVertex {
    public UserRoleVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "UserRole";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_NAME = "name";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getName() {
        return getPropertyAsString(PROPERTY_NAME);
    }

    public void setName(String name) {
        setProperty(PROPERTY_NAME, name);
    }

    public static UserRoleVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new UserRoleVertex(traversalSource, vertex);
    }

    public static UserRoleVertex findByName(GraphTraversalSource traversalSource, String name) {
        var vertex = traversalSource.V()
                .hasLabel(LABEL)
                .has(PROPERTY_NAME, name)
                .tryNext()
                .orElse(null);
        if (vertex == null)
            return null;
        return new UserRoleVertex(traversalSource, vertex);
    }
}

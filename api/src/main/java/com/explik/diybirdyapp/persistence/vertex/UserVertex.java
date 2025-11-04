package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

import static com.explik.diybirdyapp.persistence.vertex.VertexHelper.getOutgoingModels;

public class UserVertex extends AbstractVertex{
    public UserVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "User";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_NAME = "name";
    public final static String PROPERTY_USERNAME = "username";
    public final static String PROPERTY_EMAIL = "email";
    public final static String PROPERTY_PASSWORD_HASH = "passwordHash";

    public final static String EDGE_HAS_ROLE = "hasRole";

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

    public String getUsername() {
        return getPropertyAsString(PROPERTY_USERNAME);
    }

    public void setUsername(String username) {
        setProperty(PROPERTY_USERNAME, username);
    }

    public String getEmail() {
        return getPropertyAsString(PROPERTY_EMAIL);
    }

    public void setEmail(String email) {
        setProperty(PROPERTY_EMAIL, email);
    }

    public String getPasswordHash() {
        return getPropertyAsString(PROPERTY_PASSWORD_HASH);
    }

    public void setPasswordHash(String passwordHash) {
        setProperty(PROPERTY_PASSWORD_HASH, passwordHash);
    }

    public List<UserRoleVertex> getRoles() {
        return getOutgoingModels(this, EDGE_HAS_ROLE, UserRoleVertex::new);
    }

    public void addRole(UserRoleVertex role) {
        addEdgeOneToMany(EDGE_HAS_ROLE, role);
    }

    public void removeRole(UserRoleVertex role) {
        removeEdge(EDGE_HAS_ROLE, role);
    }

    public static UserVertex create(GraphTraversalSource g) {
        Vertex v = g.addV(LABEL).next();
        return new UserVertex(g, v);
    }

    public static UserVertex findWithEmail(GraphTraversalSource g, String username) {
        Vertex v = g.V().hasLabel(LABEL).has(PROPERTY_EMAIL, username).tryNext().orElse(null);
        return v == null ? null : new UserVertex(g, v);
    }
}

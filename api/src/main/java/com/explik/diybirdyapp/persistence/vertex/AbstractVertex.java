package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class AbstractVertex {
    protected final GraphTraversalSource traversalSource;
    protected Vertex vertex;

    public AbstractVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        this.traversalSource = traversalSource;
        this.vertex = vertex;
    }

    public GraphTraversalSource getUnderlyingSource() {
        return this.traversalSource;
    }

    public Vertex getUnderlyingVertex() {
        return this.vertex;
    }

    public void reload() {
        this.vertex = this.traversalSource.V(this.vertex).next();
    }

    protected void addEdgeOneToOne(String edgeLabel, AbstractVertex toVertex) {
        this.traversalSource.V(this.vertex).outE(edgeLabel).drop().iterate();
        this.traversalSource.V(this.vertex).addE(edgeLabel).to(toVertex.vertex).next();
        reload();
    }

    protected void addEdgeOneToMany(String edgeLabel, AbstractVertex toVertex) {
        this.traversalSource.V(this.vertex).addE(edgeLabel).to(toVertex.vertex).next();
        reload();
    }

    protected <T> T getProperty(String propertyKey) {
        return (T)this.vertex.property(propertyKey).value();
    }

    protected <T> T getProperty(String propertyKey, T defaultValue) {
        return this.vertex.property(propertyKey).isPresent() ? (T)this.vertex.property(propertyKey).value() : defaultValue;
    }

    protected String getPropertyAsString(String propertyKey) {
        return this.vertex.property(propertyKey).value().toString();
    }

    protected <T> void setProperty(String propertyKey, T propertyValue) {
        this.traversalSource.V(this.vertex).property(propertyKey, propertyValue).iterate();
        reload();
    }
}

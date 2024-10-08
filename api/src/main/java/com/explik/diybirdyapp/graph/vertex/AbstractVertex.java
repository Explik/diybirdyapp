package com.explik.diybirdyapp.graph.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public abstract class AbstractVertex {
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

    protected void addEdgeOneToOne(String edgeLabel, AbstractVertex toVertex) {
        this.traversalSource.V(this.vertex).outE(edgeLabel).drop().iterate();
        this.traversalSource.V(this.vertex).addE(edgeLabel).to(toVertex.vertex).next();
        this.vertex = this.traversalSource.V(vertex).next(); // Reload vertex after update
    }

    protected void addEdgeOneToMany(String edgeLabel, AbstractVertex toVertex) {
        this.traversalSource.V(this.vertex).addE(edgeLabel).to(toVertex.vertex).next();
        this.vertex = this.traversalSource.V(this.vertex).next(); // Reload vertex after update
    }

    protected <T> T getProperty(String propertyKey) {
        return (T)this.vertex.property(propertyKey).value();
    }

    protected String getPropertyAsString(String propertyKey) {
        return this.vertex.property(propertyKey).value().toString();
    }

    protected <T> void setProperty(String propertyKey, T propertyValue) {
        this.traversalSource.V(this.vertex).property(propertyKey, propertyValue).iterate();
        this.vertex = this.traversalSource.V(this.vertex).next(); // Reload vertex after update
    }
}

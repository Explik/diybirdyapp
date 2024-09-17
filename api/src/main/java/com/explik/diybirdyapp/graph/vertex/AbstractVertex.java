package com.explik.diybirdyapp.graph.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public abstract class AbstractVertex {
    protected final GraphTraversalSource traversalSource;
    protected final Vertex vertex;

    public AbstractVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        this.traversalSource = traversalSource;
        this.vertex = vertex;
    }

    protected void addEdgeOneToOne(String edgeLabel, AbstractVertex toVertex) {
        traversalSource.V(this.vertex).out(edgeLabel).forEachRemaining(Vertex::remove);
        traversalSource.V(this.vertex).addE(edgeLabel).to(toVertex.vertex).next();
    }

    protected void addEdgeOneToMany(String edgeLabel, AbstractVertex toVertex) {
        traversalSource.V(this.vertex).addE(edgeLabel).to(toVertex.vertex).next();
    }

    protected <T> T getProperty(String propertyKey) {
        return (T)this.vertex.property(propertyKey).value();
    }

    protected String getPropertyAsString(String propertyKey) {
        return this.vertex.property(propertyKey).value().toString();
    }

    protected <T> void setProperty(String propertyKey, T propertyValue) {
        this.traversalSource.V(this.vertex).property(propertyKey, propertyValue).iterate();
    }
}

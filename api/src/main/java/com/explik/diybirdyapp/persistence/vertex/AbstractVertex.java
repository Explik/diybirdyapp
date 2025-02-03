package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
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

    public String getLabel() {
        return this.vertex.label();
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

    protected void addOrderedEdgeOneToOne(String edgeLabel, AbstractVertex toVertex, String orderProperty, Object orderValue) {
        this.traversalSource.V(this.vertex).outE(edgeLabel).drop().iterate();
        this.traversalSource.V(this.vertex).addE(edgeLabel).to(toVertex.vertex).property(orderProperty, orderValue).next();
        reload();
    }

    protected void addOrderedEdgeOneToMany(String edgeLabel, AbstractVertex toVertex, String orderProperty, Object orderValue) {
        this.traversalSource.V(this.vertex).addE(edgeLabel).to(toVertex.vertex).property(orderProperty, orderValue).next();
        reload();
    }


    protected void removeEdges(String edgeLabel) {
        this.traversalSource.V(this.vertex).outE(edgeLabel).drop().iterate();
        reload();
    }

    protected void removeEdge(String edgeLabel, AbstractVertex toVertex) {
        this.traversalSource.V(this.vertex).outE(edgeLabel).where(__.inV().is(toVertex.vertex)).drop().iterate();
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

    protected boolean getPropertyAsBoolean(String propertyKey) {
        return (boolean)getProperty(propertyKey);
    }

    protected boolean getPropertyAsBoolean(String propertyKey, boolean defaultValue) {
        return (boolean)getProperty(propertyKey, defaultValue);
    }

    protected <T> void setProperty(String propertyKey, T propertyValue) {
        this.traversalSource.V(this.vertex).property(propertyKey, propertyValue).iterate();
        reload();
    }
}

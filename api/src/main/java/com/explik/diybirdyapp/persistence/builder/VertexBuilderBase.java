package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public abstract class VertexBuilderBase<T extends AbstractVertex> implements VertexBuilder<T>, VertexBuilderFactoriesInjectable {
    protected VertexBuilderFactories factories;

    @Override
    public abstract T build(GraphTraversalSource traversalSource);

    @Override
    public void injectFactories(VertexBuilderFactories factories) {
        this.factories = factories;
    }
}

package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VertexHelper {
    public static ContentVertex createContent(GraphTraversalSource traversalSource, Vertex vertex) {
        if (vertex.label().equals(AudioContentVertex.LABEL))
            return new AudioContentVertex(traversalSource, vertex);
        if (vertex.label().equals(FlashcardVertex.LABEL))
            return new FlashcardVertex(traversalSource, vertex);
        if (vertex.label().equals(TextContentVertex.LABEL))
            return new TextContentVertex(traversalSource, vertex);
        if(vertex.label().equals(ImageContentVertex.LABEL))
            return new ImageContentVertex(traversalSource, vertex);
        if (vertex.label().equals(VideoContentVertex.LABEL))
            return new VideoContentVertex(traversalSource, vertex);

        throw new RuntimeException("Unknown content type: " + vertex.label());
    }

    public static ContentVertex createContent(ContentVertex contentVertex){
        return createContent(contentVertex.getUnderlyingSource(), contentVertex.getUnderlyingVertex());
    }

    public static <T extends AbstractVertex> T getOutgoingModel(AbstractVertex vertex, String edgeLabel, BiFunction<GraphTraversalSource, Vertex, T> mapper) {
        var traversalSource = vertex.getUnderlyingSource();
        var v = traversalSource.V(vertex.getUnderlyingVertex()).out(edgeLabel).next();
        return mapper.apply(traversalSource, v);
    }

    public static <T extends AbstractVertex> T getIngoingModel(AbstractVertex vertex, String edgeLabel, BiFunction<GraphTraversalSource, Vertex, T> mapper) {
        var traversalSource = vertex.getUnderlyingSource();
        var v = traversalSource.V(vertex.getUnderlyingVertex()).in(edgeLabel).next();
        return mapper.apply(traversalSource, v);
    }

    public static <T extends AbstractVertex> T getOptionalOutgoingModel(AbstractVertex vertex, String edgeLabel, BiFunction<GraphTraversalSource, Vertex, T> mapper) {
        var traversalSource = vertex.getUnderlyingSource();
        var v = traversalSource.V(vertex.getUnderlyingVertex()).out(edgeLabel).tryNext().orElse(null);
        return v == null ? null : mapper.apply(traversalSource, v);
    }

    public static <T extends AbstractVertex> T getOptionalIngoingModel(AbstractVertex vertex, String edgeLabel, BiFunction<GraphTraversalSource, Vertex, T> mapper) {
        var traversalSource = vertex.getUnderlyingSource();
        var v = traversalSource.V(vertex.getUnderlyingVertex()).in(edgeLabel).tryNext().orElse(null);
        return v == null ? null : mapper.apply(traversalSource, v);
    }

    public static <T extends AbstractVertex> List<T> getOutgoingModels(AbstractVertex vertex, String edgeLabel, BiFunction<GraphTraversalSource, Vertex, T> mapper) {
        var traversalSource = vertex.getUnderlyingSource();
        var vertices = traversalSource.V(vertex.getUnderlyingVertex()).out(edgeLabel).toList();
        return vertices.stream()
                .map(v -> mapper.apply(traversalSource, v))
                .toList();
    }

    public static <T extends AbstractVertex> List<T> getIngoingModels(AbstractVertex vertex, String edgeLabel, BiFunction<GraphTraversalSource, Vertex, T> mapper) {
        var traversalSource = vertex.getUnderlyingSource();
        var vertices = traversalSource.V(vertex.getUnderlyingVertex()).in(edgeLabel).toList();
        return vertices.stream()
                .map(v -> mapper.apply(traversalSource, v))
                .toList();
    }

    public static <T extends AbstractVertex> List<T> getOrderedOutgoingModels(AbstractVertex vertex, String edgeLabel, String orderProperty, BiFunction<GraphTraversalSource, Vertex, T> mapper) {
        var traversalSource = vertex.getUnderlyingSource();
        var vertices = traversalSource.V(vertex.getUnderlyingVertex())
            .outE(edgeLabel)
            .order().by(orderProperty)
            .inV()
            .toList();

        return vertices.stream()
            .map(v -> mapper.apply(traversalSource, v))
            .toList();
    }

    public static <T extends AbstractVertex> List<T> getOrderedIngoingModels(AbstractVertex vertex, String edgeLabel, String orderProperty, BiFunction<GraphTraversalSource, Vertex, T> mapper) {
        var traversalSource = vertex.getUnderlyingSource();
        var vertices = traversalSource.V(vertex.getUnderlyingVertex())
            .inE(edgeLabel)
            .order().by(orderProperty)
            .outV()
            .toList();

        return vertices.stream()
            .map(v -> mapper.apply(traversalSource, v))
            .toList();
    }

    public static <T> T getOutgoingProperty(AbstractVertex vertex, String edgeLabel, String property) {
        var traversalSource = vertex.getUnderlyingSource();
        var edgeProperty = traversalSource.V(vertex.getUnderlyingVertex())
                .outE(edgeLabel)
                .values(property)
                .next();
        return (T)edgeProperty;
    }

    public static <T> T getOptionalOutgoingProperty(AbstractVertex vertex, String edgeLabel, String property) {
        var traversalSource = vertex.getUnderlyingSource();
        var edgeProperty = traversalSource.V(vertex.getUnderlyingVertex())
                .outE(edgeLabel)
                .values(property)
                .tryNext()
                .orElse(null);
        return (T)edgeProperty;
    }

    public static <T> T getIngoingProperty(AbstractVertex vertex, String edgeLabel, String property) {
        var traversalSource = vertex.getUnderlyingSource();
        var edgeProperty = traversalSource.V(vertex.getUnderlyingVertex())
                .inE(edgeLabel)
                .values(property)
                .next();
        return (T)edgeProperty;
    }

    public static <T> T getOptionalIngoingProperty(AbstractVertex vertex, String edgeLabel, String property) {
        var traversalSource = vertex.getUnderlyingSource();
        var edgeProperty = traversalSource.V(vertex.getUnderlyingVertex())
                .inE(edgeLabel)
                .values(property)
                .tryNext()
                .orElse(null);
        return (T)edgeProperty;
    }

    public static <T> void setOutgoingProperty(AbstractVertex vertex, String edgeLabel, String property, T propertyValue) {
        var traversalSource = vertex.getUnderlyingSource();
        traversalSource.V(vertex.getUnderlyingVertex())
                .outE(edgeLabel)
                .property(property, propertyValue)
                .iterate();
        vertex.reload();
    }

    public static <T> void setIngoingProperty(AbstractVertex vertex, String edgeLabel, String property, T propertyValue) {
        var traversalSource = vertex.getUnderlyingSource();
        traversalSource.V(vertex.getUnderlyingVertex())
                .inE(edgeLabel)
                .property(property, propertyValue)
                .iterate();
        vertex.reload();
    }
}

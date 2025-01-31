package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

import static org.apache.tinkerpop.gremlin.process.traversal.P.*;

public class WordVertex extends AbstractVertex {
    public WordVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "word";

    public static final String EDGE_LANGUAGE = "hasLanguage";
    public static final String EDGE_EXAMPLE = "hasExample";
    public static final String EDGE_MAIN_EXAMPLE = "hasMainExample";

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_VALUE = "value";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getValue() {
        return getPropertyAsString(PROPERTY_VALUE);
    }

    public void setValue(String value) {
        setProperty(PROPERTY_VALUE, value);
    }

    public LanguageVertex getLanguage() {
        return VertexHelper.getOutgoingModel(this, EDGE_LANGUAGE, LanguageVertex::new);
    }

    public void setLanguage(AbstractVertex languageVertex) {
        addEdgeOneToOne(EDGE_LANGUAGE, languageVertex);
    }

    public void addExample(AbstractVertex exampleVertex) {
        addEdgeOneToMany(EDGE_EXAMPLE, exampleVertex);
    }

    public List<? extends TextContentVertex> getExamples() {
        return VertexHelper.getOutgoingModels(this, EDGE_EXAMPLE, TextContentVertex::new);
    }

    public void setMainExample(AbstractVertex exampleVertex) {
        addEdgeOneToOne(EDGE_MAIN_EXAMPLE, exampleVertex);
    }

    public TextContentVertex getMainExample() {
        return VertexHelper.getOutgoingModel(this, EDGE_MAIN_EXAMPLE, TextContentVertex::new);
    }

    public static WordVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new WordVertex(traversalSource, vertex);
    }

    public static WordVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertexQuery = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id);
        if (!vertexQuery.hasNext())
            return null;
        return new WordVertex(traversalSource, vertexQuery.next());
    }

    public static WordVertex findByValue(GraphTraversalSource traversalSource, String value) {
        var vertexQuery = traversalSource.V().hasLabel(LABEL).has(PROPERTY_VALUE, value);
        if (!vertexQuery.hasNext())
            return null;
        return new WordVertex(traversalSource, vertexQuery.next());
    }

    public static List<WordVertex> findByValues(GraphTraversalSource traversalSource, List<String> values) {
        var vertices = traversalSource.V().hasLabel(LABEL).has(PROPERTY_VALUE, within(values)).toList();
        return vertices.stream()
            .map(v -> new WordVertex(traversalSource, v))
            .toList();
    }

    public static void removeWordsFromTextContent(GraphTraversalSource traversalSource, TextContentVertex textContent) {
        var wordVertices = traversalSource.V().hasLabel(LABEL).out(EDGE_EXAMPLE).hasId(textContent.getId()).toList();
        for (var wordVertex : wordVertices) {
            traversalSource.V(wordVertex).outE(EDGE_EXAMPLE).hasId(textContent.getId()).drop().iterate();
        }
    }

    public static List<WordVertex> getAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel(LABEL).toList();
        return vertices.stream()
                .map(v -> new WordVertex(traversalSource, v))
                .toList();
    }
}

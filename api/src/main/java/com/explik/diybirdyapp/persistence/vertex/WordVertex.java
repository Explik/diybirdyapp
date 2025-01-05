package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class WordVertex extends AbstractVertex implements IdentifiableVertex {
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
        var languageVertex = traversalSource.V(vertex).out(EDGE_LANGUAGE).next();
        return new LanguageVertex(traversalSource, languageVertex);
    }

    public void setLanguage(AbstractVertex languageVertex) {
        addEdgeOneToOne(EDGE_LANGUAGE, languageVertex);
    }

    public void addExample(AbstractVertex exampleVertex) {
        addEdgeOneToMany(EDGE_EXAMPLE, exampleVertex);
    }

    public List<? extends TextContentVertex> getExamples() {
        var exampleVertices = traversalSource.V(vertex).out(EDGE_EXAMPLE).toList();
        return exampleVertices.stream()
                .map(v -> new TextContentVertex(traversalSource, v))
                .toList();
    }

    public void setMainExample(AbstractVertex exampleVertex) {
        addEdgeOneToOne(EDGE_MAIN_EXAMPLE, exampleVertex);
    }

    public TextContentVertex getMainExample() {
        var exampleVertex = traversalSource.V(vertex).out(EDGE_MAIN_EXAMPLE).next();
        return new TextContentVertex(traversalSource, exampleVertex);
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

    public static List<WordVertex> getAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel(LABEL).toList();
        return vertices.stream()
                .map(v -> new WordVertex(traversalSource, v))
                .toList();
    }
}

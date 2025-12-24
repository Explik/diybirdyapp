package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import java.util.List;

public class TextContentVertex extends ContentVertex {
    public TextContentVertex(AbstractVertex vertex) {
        super(vertex.getUnderlyingSource(), vertex.getUnderlyingVertex());
    }

    public TextContentVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "textContent";

    public static final String EDGE_LANGUAGE = "hasLanguage";
    public static final String EDGE_PRONUNCIATION = "hasPronunciation";
    public static final String EDGE_MAIN_PRONUNCIATION = "hasMainPronunciation";

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_VALUE = "value";

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

    public List<PronunciationVertex> getPronunciations() {
        return VertexHelper.getOutgoingModels(this, EDGE_PRONUNCIATION, PronunciationVertex::new);
    }

    public static TextContentVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new TextContentVertex(traversalSource, vertex);
    }

    public static TextContentVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id).tryNext();
        return vertex.map(v -> new TextContentVertex(traversalSource, v)).orElse(null);
    }
}

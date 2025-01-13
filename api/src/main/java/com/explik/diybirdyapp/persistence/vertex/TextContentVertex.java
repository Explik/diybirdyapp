package com.explik.diybirdyapp.persistence.vertex;

import com.explik.diybirdyapp.model.ExerciseContentTextModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import java.util.List;

public class TextContentVertex extends ContentVertex {
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
        var languageVertex = traversalSource.V(vertex).out(EDGE_LANGUAGE).next();
        return new LanguageVertex(traversalSource, languageVertex);
    }

    public void setLanguage(AbstractVertex languageVertex) {
        addEdgeOneToOne(EDGE_LANGUAGE, languageVertex);
    }

    public List<PronunciationVertex> getPronunciations() {
        var vertices = traversalSource.V(vertex).out(EDGE_PRONUNCIATION).toList();
        return vertices.stream().map(v -> new PronunciationVertex(traversalSource, v)).toList();
    }

    public void addPronunciation(AbstractVertex pronunciationVertex) {
        addEdgeOneToMany(EDGE_PRONUNCIATION, pronunciationVertex);
    }

    public void removePronunciation(AbstractVertex pronunciationVertex) {
        removeEdge(EDGE_PRONUNCIATION, pronunciationVertex);
    }

    public boolean hasMainPronunciation() {
        return traversalSource.V(vertex).out(EDGE_MAIN_PRONUNCIATION).hasNext();
    }

    public PronunciationVertex getMainPronunciation() {
        var query = traversalSource.V(vertex).out(EDGE_MAIN_PRONUNCIATION);
        return query.hasNext() ? new PronunciationVertex(traversalSource, query.next()) : null;
    }

    public void setMainPronunciation(AbstractVertex pronunciationVertex) {
        addEdgeOneToOne(EDGE_MAIN_PRONUNCIATION, pronunciationVertex);
    }

    public ExerciseContentTextModel toExerciseContentTextModel() {
        var model = new ExerciseContentTextModel();
        model.setId(getId());
        model.setText(getValue());
        return model;
    }

    public static TextContentVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new TextContentVertex(traversalSource, vertex);
    }
}

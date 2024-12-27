package com.explik.diybirdyapp.persistence.vertex;

import com.explik.diybirdyapp.model.ExerciseContentTextModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class TextContentVertex extends AbstractVertex implements IdentifiableVertex {
    public TextContentVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "textContent";

    public static final String EDGE_LANGUAGE = "hasLanguage";

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

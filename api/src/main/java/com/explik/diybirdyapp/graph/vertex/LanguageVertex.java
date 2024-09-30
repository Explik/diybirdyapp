package com.explik.diybirdyapp.graph.vertex;

import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class LanguageVertex extends AbstractVertex {
    public LanguageVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "language";

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_ABBREVIATION = "abbreviation";
    public static final String PROPERTY_NAME = "name";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getAbbreviation() {
        return getPropertyAsString(PROPERTY_ABBREVIATION);
    }

    public void setAbbreviation(String abbreviation) {
        setProperty(PROPERTY_ABBREVIATION, abbreviation);
    }

    public String getName() {
        return getPropertyAsString(PROPERTY_NAME);
    }

    public void setName(String name) {
        setProperty(PROPERTY_NAME, name);
    }

    public FlashcardLanguageModel toFlashcardLanguageModel() {
        var model = new FlashcardLanguageModel();
        model.setId(getId());
        model.setAbbreviation(getAbbreviation());
        model.setName(getName());
        return model;
    }

    public static LanguageVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new LanguageVertex(traversalSource, vertex);
    }

    public static LanguageVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertexQuery = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id);
        if (!vertexQuery.hasNext())
            return null;
        return new LanguageVertex(traversalSource, vertexQuery.next());
    }

    public static LanguageVertex findByName(GraphTraversalSource traversalSource, String name) {
        var vertexQuery = traversalSource.V().hasLabel(LABEL).has(PROPERTY_NAME, name);
        if (!vertexQuery.hasNext())
            return null;
        return new LanguageVertex(traversalSource, vertexQuery.next());
    }

    public static LanguageVertex findByAbbreviation(GraphTraversalSource traversalSource, String abbreviation) {
        var vertexQuery = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ABBREVIATION, abbreviation);
        if(!vertexQuery.hasNext())
            return null;
        return new LanguageVertex(traversalSource, vertexQuery.next());
    }

    public static List<LanguageVertex> findAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel(LABEL).toList();

        return vertices.stream()
                .map(v -> new LanguageVertex(traversalSource, v))
                .toList();
    }
}

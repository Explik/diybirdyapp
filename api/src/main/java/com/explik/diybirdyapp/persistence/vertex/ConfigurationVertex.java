package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class ConfigurationVertex extends AbstractVertex {
    public ConfigurationVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static final String LABEL = "configuration";

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_TYPE = "type";

    public static final String EDGE_LANGUAGE = "has_language";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        super.setProperty(PROPERTY_ID, id);
    }

    public String getType() {
        return getPropertyAsString(PROPERTY_TYPE);
    }

    public void setType(String type) {
        super.setProperty(PROPERTY_TYPE, type);
    }

    @Override
    public <T> T getProperty(String key) {
        if (key.equals(PROPERTY_ID))
            throw new IllegalArgumentException("Use getId() to get the id property");
        if (key.equals(PROPERTY_TYPE))
            throw new IllegalArgumentException("Use getType() to get the type property");

        return super.getProperty(key);
    }

    @Override
    public <T> T getProperty(String key, T defaultValue) {
        if (key.equals(PROPERTY_ID))
            throw new IllegalArgumentException("Use getId() to get the id property");
        if (key.equals(PROPERTY_TYPE))
            throw new IllegalArgumentException("Use getType() to get the type property");

        return super.getProperty(key, defaultValue);
    }

    @Override
    public void setProperty(String key, Object value) {
        if (key.equals(PROPERTY_ID))
            throw new IllegalArgumentException("Use setId() to set the id property");
        if (key.equals(PROPERTY_TYPE))
            throw new IllegalArgumentException("Use setType() to set the type property");

        super.setProperty(key, value);
    }

    public List<LanguageVertex> getLanguages() {
        return VertexHelper.getOutgoingModels(
            this,
            EDGE_LANGUAGE,
            LanguageVertex::new
        );
    }

    public void addLanguage(LanguageVertex languageVertex) {
        addEdgeOneToMany(EDGE_LANGUAGE, languageVertex);
    }

    public void removeLanguage(LanguageVertex languageVertex) {
        removeEdge(EDGE_LANGUAGE, languageVertex);
    }

    public void clearLanguages() {
        removeEdges(EDGE_LANGUAGE);
    }

    public static ConfigurationVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ConfigurationVertex(traversalSource, vertex);
    }

    public static ConfigurationVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertexQuery = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id);
        if (!vertexQuery.hasNext())
            return null;
        return new ConfigurationVertex(traversalSource, vertexQuery.next());
    }

    public static List<ConfigurationVertex> findByLanguage(LanguageVertex languageVertex) {
        var traversalSource = languageVertex.getUnderlyingSource();
        var vertexQuery = traversalSource.V(languageVertex.getUnderlyingVertex())
                .inE(EDGE_LANGUAGE)
                .outV()
                .hasLabel(LABEL);

        return vertexQuery
                .toStream()
                .map(v -> new ConfigurationVertex(traversalSource, v))
                .toList();
    }

    public static List<ConfigurationVertex> findByLanguageAndType(LanguageVertex languageVertex, String configurationType) {
        var traversalSource = languageVertex.getUnderlyingSource();
        var vertexQuery = traversalSource.V(languageVertex.getUnderlyingVertex())
            .inE(EDGE_LANGUAGE)
            .outV()
            .hasLabel(LABEL)
            .has(PROPERTY_TYPE, configurationType);

        return vertexQuery
                .toStream()
                .map(v -> new ConfigurationVertex(traversalSource, v))
                .toList();
    }
}

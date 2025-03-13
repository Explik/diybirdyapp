package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class SpeechToTextConfigVertex extends AbstractVertex {
    public SpeechToTextConfigVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static String LABEL = "SpeechToTextConfig";
    public static String PROPERTY_ID = "id";
    public static String PROPERTY_LANGUAGE_CODE = "languageCode";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getLanguageCode() {
        return getProperty(PROPERTY_LANGUAGE_CODE);
    }

    public void setLanguageCode(String languageCode) {
        setProperty(PROPERTY_LANGUAGE_CODE, languageCode);
    }

    public static SpeechToTextConfigVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new SpeechToTextConfigVertex(traversalSource, vertex);
    }
}

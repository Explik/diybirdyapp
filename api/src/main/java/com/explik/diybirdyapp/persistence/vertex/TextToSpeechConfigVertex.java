package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.assign.TypeCasting;

import java.util.List;

public class TextToSpeechConfigVertex extends AbstractVertex {
    public TextToSpeechConfigVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public static String LABEL = "TextToSpeechConfig";
    public static String PROPERTY_ID = "id";
    public static String PROPERTY_LANGUAGE_CODE = "languageCode";
    public static String PROPERTY_VOICE_NAME = "voiceName";
    public static String EDGE_LANGUAGE = "hasLanguage";

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

    public String getVoiceName() {
        return getProperty(PROPERTY_VOICE_NAME);
    }

    public void setVoiceName(String voiceName) {
        setProperty(PROPERTY_VOICE_NAME, voiceName);
    }

    public void setLanguage(LanguageVertex language) {
        addEdgeOneToOne(EDGE_LANGUAGE, language);
    }

    public LanguageVertex getLanguage() {
        var languageVertex = traversalSource.V(vertex).out(EDGE_LANGUAGE).next();
        return new LanguageVertex(traversalSource, languageVertex);
    }

    public static TextToSpeechConfigVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new TextToSpeechConfigVertex(traversalSource, vertex);
    }

    public static TextToSpeechConfigVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().has(PROPERTY_ID, id).next();
        return new TextToSpeechConfigVertex(traversalSource, vertex);
    }

    public static List<TextToSpeechConfigVertex> findByLanguageId(GraphTraversalSource traversalSource, String languageId) {
        var vertices = traversalSource.V()
                .has(LanguageVertex.LABEL, LanguageVertex.PROPERTY_ID, languageId)
                .in(EDGE_LANGUAGE)
                .hasLabel(LABEL)
                .toList();
        return vertices.stream().map(v -> new TextToSpeechConfigVertex(traversalSource, v)).toList();
    }
}

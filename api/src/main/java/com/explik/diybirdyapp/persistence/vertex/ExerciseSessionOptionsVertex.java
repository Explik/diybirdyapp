package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class ExerciseSessionOptionsVertex extends AbstractVertex {
    public ExerciseSessionOptionsVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseSessionOptions";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_TYPE = "type";
    public final static String PROPERTY_TEXT_TO_SPEECH_ENABLED = "textToSpeechEnabled";
    public final static String PROPERTY_INITIAL_FLASHCARD_LANGUAGE_ID = "initialFlashcardLanguageId";
    public final static String PROPERTY_RETYPE_CORRECT_ANSWER = "retypeCorrectAnswer";

    public final static String EDGE_ANSWER_LANGUAGE = "hasAnswerLanguage";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getType() {
        return getPropertyAsString(PROPERTY_TYPE);
    }

    public void setType(String type) {
        setProperty(PROPERTY_TYPE, type);
    }

    public boolean getTextToSpeechEnabled() {
        return getProperty(PROPERTY_TEXT_TO_SPEECH_ENABLED, false);
    }

    public void setTextToSpeechEnabled(boolean textToSpeechEnabled) {
        setProperty(PROPERTY_TEXT_TO_SPEECH_ENABLED, textToSpeechEnabled);
    }

    public boolean getRetypeCorrectAnswer() {
        return getProperty(PROPERTY_RETYPE_CORRECT_ANSWER, false);
    }

    public void setRetypeCorrectAnswer(boolean retypeCorrectAnswer) {
        setProperty(PROPERTY_RETYPE_CORRECT_ANSWER, retypeCorrectAnswer);
    }

    public static ExerciseSessionOptionsVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseSessionOptionsVertex(traversalSource, vertex);
    }

    public String getInitialFlashcardLanguageId() {
        return getPropertyAsString(PROPERTY_INITIAL_FLASHCARD_LANGUAGE_ID, null);
    }

    public void setInitialFlashcardLanguageId(String initialFlashcardLanguageId) {
        setProperty(PROPERTY_INITIAL_FLASHCARD_LANGUAGE_ID, initialFlashcardLanguageId);
    }

    public List<LanguageVertex> getAnswerLanguages() {
        return VertexHelper.getOutgoingModels(this, EDGE_ANSWER_LANGUAGE, LanguageVertex::new);
    }

    public void addAnswerLanguage(LanguageVertex language) {
        addEdgeOneToMany(EDGE_ANSWER_LANGUAGE, language);
    }

    public void removeAnswerLanguage(LanguageVertex language) {
        removeEdge(EDGE_ANSWER_LANGUAGE, language);
    }

    public ExerciseSessionVertex getSession() {
        return VertexHelper.getIngoingModel(this, ExerciseSessionVertex.EDGE_OPTIONS, ExerciseSessionVertex::new);
    }
}

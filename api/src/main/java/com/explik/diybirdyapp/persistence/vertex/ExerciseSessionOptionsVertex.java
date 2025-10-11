package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ExerciseSessionOptionsVertex extends AbstractVertex {
    public ExerciseSessionOptionsVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseSessionOptions";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_FLASHCARD_SIDE = "flashcardSide";
    public final static String PROPERTY_TEXT_TO_SPEECH_ENABLED = "textToSpeechEnabled";
    public final static String PROPERTY_INITIAL_FLASHCARD_LANGUAGE_ID = "initialFlashcardLanguageId";
    public final static String PROPERTY_RETYPE_CORRECT_ANSWER = "retypeCorrectAnswer";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public String getFlashcardSide() {
        return getPropertyAsString(PROPERTY_FLASHCARD_SIDE, null);
    }

    public void setFlashcardSide(String flashcardSide) {
        setProperty(PROPERTY_FLASHCARD_SIDE, flashcardSide);
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
}

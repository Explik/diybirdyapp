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
    public final static String PROPERTY_INCLUDE_REVIEW_EXERCISES = "includeReviewExercises";
    public final static String PROPERTY_INCLUDE_MULTIPLE_CHOICE_EXERCISES = "includeMultipleChoiceExercises";
    public final static String PROPERTY_INCLUDE_WRITING_EXERCISES = "includeWritingExercises";
    public final static String PROPERTY_INCLUDE_LISTENING_EXERCISES = "includeListeningExercises";
    public final static String PROPERTY_INCLUDE_PRONUNCIATION_EXERCISES = "includePronunciationExercises";
    public final static String PROPERTY_RETYPE_CORRECT_ANSWER = "retypeCorrectAnswer";
    public final static String PROPERTY_SHUFFLE_FLASHCARDS = "shuffleFlashcards";
    public final static String PROPERTY_ALGORITHM = "algorithm";
    public final static String PROPERTY_INITIALLY_HIDE_OPTIONS = "initiallyHideOptions";

    public final static String EDGE_ANSWER_LANGUAGE = "hasAnswerLanguage";
    public final static String EDGE_MULTIPLE_CHOICE_ANSWER_LANGUAGE = "hasMultipleChoiceAnswerLanguage";
    public final static String EDGE_WRITING_ANSWER_LANGUAGE = "hasWritingAnswerLanguage";
    public final static String EDGE_TARGET_LANGUAGE = "hasTargetLanguage";
    public final static String EDGE_EXERCISE_TYPES = "hasExerciseType";

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

    public LanguageVertex getTargetLanguage() {
        return VertexHelper.getOptionalOutgoingModel(this, EDGE_TARGET_LANGUAGE, LanguageVertex::new);
    }

    public void setTargetLanguage(LanguageVertex language) {
        addEdgeOneToOne(EDGE_TARGET_LANGUAGE, language);
    }

    public void removeTargetLanguage() {
        removeEdges(EDGE_TARGET_LANGUAGE);
    }

    public String getAlgorithm() {
        return getPropertyAsString(PROPERTY_ALGORITHM, null);
    }

    public void setAlgorithm(String algorithm) {
        setProperty(PROPERTY_ALGORITHM, algorithm);
    }

    public boolean getIncludeReviewExercises() {
        return getProperty(PROPERTY_INCLUDE_REVIEW_EXERCISES, false);
    }

    public void setIncludeReviewExercises(boolean includeReviewExercises) {
        setProperty(PROPERTY_INCLUDE_REVIEW_EXERCISES, includeReviewExercises);
    }

    public boolean getIncludeMultipleChoiceExercises() {
        return getProperty(PROPERTY_INCLUDE_MULTIPLE_CHOICE_EXERCISES, false);
    }

    public void setIncludeMultipleChoiceExercises(boolean includeMultipleChoiceExercises) {
        setProperty(PROPERTY_INCLUDE_MULTIPLE_CHOICE_EXERCISES, includeMultipleChoiceExercises);
    }

    public boolean getIncludeWritingExercises() {
        return getProperty(PROPERTY_INCLUDE_WRITING_EXERCISES, false);
    }

    public void setIncludeWritingExercises(boolean includeWritingExercises) {
        setProperty(PROPERTY_INCLUDE_WRITING_EXERCISES, includeWritingExercises);
    }

    public boolean getIncludeListeningExercises() {
        return getProperty(PROPERTY_INCLUDE_LISTENING_EXERCISES, false);
    }

    public void setIncludeListeningExercises(boolean includeListeningExercises) {
        setProperty(PROPERTY_INCLUDE_LISTENING_EXERCISES, includeListeningExercises);
    }

    public boolean getIncludePronunciationExercises() {
        return getProperty(PROPERTY_INCLUDE_PRONUNCIATION_EXERCISES, false);
    }

    public void setIncludePronunciationExercises(boolean includePronunciationExercises) {
        setProperty(PROPERTY_INCLUDE_PRONUNCIATION_EXERCISES, includePronunciationExercises);
    }

    public boolean getInitiallyHideOptions() {
        return getProperty(PROPERTY_INITIALLY_HIDE_OPTIONS, false);
    }

    public void setInitiallyHideOptions(boolean initiallyHideOptions) {
        setProperty(PROPERTY_INITIALLY_HIDE_OPTIONS, initiallyHideOptions);
    }

    public boolean getShuffleFlashcards() {
        return getProperty(PROPERTY_SHUFFLE_FLASHCARDS, false);
    }

    public void setShuffleFlashcards(boolean shuffleFlashcards) {
        setProperty(PROPERTY_SHUFFLE_FLASHCARDS, shuffleFlashcards);
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

    public List<LanguageVertex> getMultipleChoiceAnswerLanguages() {
        return VertexHelper.getOutgoingModels(this, EDGE_MULTIPLE_CHOICE_ANSWER_LANGUAGE, LanguageVertex::new);
    }

    public void addMultipleChoiceAnswerLanguage(LanguageVertex language) {
        addEdgeOneToMany(EDGE_MULTIPLE_CHOICE_ANSWER_LANGUAGE, language);
    }

    public void removeMultipleChoiceAnswerLanguage(LanguageVertex language) {
        removeEdge(EDGE_MULTIPLE_CHOICE_ANSWER_LANGUAGE, language);
    }

    public List<LanguageVertex> getWritingAnswerLanguages() {
        return VertexHelper.getOutgoingModels(this, EDGE_WRITING_ANSWER_LANGUAGE, LanguageVertex::new);
    }

    public void addWritingAnswerLanguage(LanguageVertex language) {
        addEdgeOneToMany(EDGE_WRITING_ANSWER_LANGUAGE, language);
    }

    public void removeWritingAnswerLanguage(LanguageVertex language) {
        removeEdge(EDGE_WRITING_ANSWER_LANGUAGE, language);
    }

    public ExerciseSessionVertex getSession() {
        return VertexHelper.getIngoingModel(this, ExerciseSessionVertex.EDGE_OPTIONS, ExerciseSessionVertex::new);
    }

    public void addExerciseType(ExerciseTypeVertex exerciseType) {
        addEdgeOneToMany(EDGE_EXERCISE_TYPES, exerciseType);
    }

    public void removeExerciseType(ExerciseTypeVertex exerciseType) {
        removeEdge(EDGE_EXERCISE_TYPES, exerciseType);
    }

    public List<ExerciseTypeVertex> getExerciseTypes() {
        return VertexHelper.getOutgoingModels(this, EDGE_EXERCISE_TYPES, ExerciseTypeVertex::new);
    }
}

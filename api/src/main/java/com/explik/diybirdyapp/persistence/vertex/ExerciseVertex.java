package com.explik.diybirdyapp.persistence.vertex;

import com.explik.diybirdyapp.model.ExerciseModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class ExerciseVertex extends AbstractVertex {
    public ExerciseVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exercise";

    public final static String EDGE_ANSWER = "hasAnswer";
    public final static String EDGE_CONTENT = "hasContent";
    public final static String EDGE_SESSION = "hasSession";
    public final static String EDGE_OPTION = "hasOption";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_TARGET_LANGUAGE = "targetLanguage";
    public final static String PROPERTY_FLASHCARD_SIDE = "flashcardSide";
    public final static String PROPERTY_TYPE = "exerciseType";

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

    public ContentVertex getContent() {
        var contentVertex = traversalSource.V(vertex).out(EDGE_CONTENT).next();
        if (contentVertex.label().equals(TextContentVertex.LABEL))
            return new TextContentVertex(traversalSource, contentVertex);
        else if(contentVertex.label().equals(FlashcardVertex.LABEL))
            return new FlashcardVertex(traversalSource, contentVertex);

        throw new RuntimeException("Unknown content type: " + contentVertex.label());
    }

    public FlashcardVertex getFlashcardAnswer() {
        var answerVertex = traversalSource.V(vertex).out(EDGE_ANSWER).next();
        return new FlashcardVertex(traversalSource, answerVertex);
    }

    public TextContentVertex getTextContentAnswer() {
        var answerVertex = traversalSource.V(vertex).out(EDGE_ANSWER).next();
        return new TextContentVertex(traversalSource, answerVertex);
    }

    public void setAnswer(AbstractVertex answer) {
        addEdgeOneToOne(EDGE_ANSWER, answer);
    }

    public TextContentVertex getTextContent() {
        var textContentVertex = traversalSource.V(vertex).out(EDGE_CONTENT).next();
        return new TextContentVertex(traversalSource, textContentVertex);
    }

    public FlashcardVertex getFlashcardContent() {
        var flashcardContentVertex = traversalSource.V(vertex).out(EDGE_CONTENT).next();
        return new FlashcardVertex(traversalSource, flashcardContentVertex);
    }

    public void setContent(AbstractVertex outVertex) {
        addEdgeOneToOne(EDGE_CONTENT, outVertex);
    }

    public String getTargetLanguage() {
        return getPropertyAsString(PROPERTY_TARGET_LANGUAGE);
    }

    public void setTargetLanguage(String targetLanguage) {
        setProperty(PROPERTY_TARGET_LANGUAGE, targetLanguage);
    }

    public String getFlashcardSide() { return getPropertyAsString(PROPERTY_FLASHCARD_SIDE); }

    public void setFlashcardSide(String flashcardSide) { setProperty(PROPERTY_FLASHCARD_SIDE, flashcardSide); }

    public void addOption(AbstractVertex option) {
        addEdgeOneToMany(EDGE_OPTION, option);
    }

    public List<? extends TextContentVertex> getTextContentOptions() {
        var choiceVertices = traversalSource.V(vertex).out(EDGE_OPTION).toList();
        return choiceVertices.stream()
                .map(v -> new TextContentVertex(traversalSource, v))
                .toList();
    }

    public List<? extends FlashcardVertex> getFlashcardOptions() {
        var choiceVertices = traversalSource.V(vertex).out(EDGE_OPTION).toList();
        return choiceVertices.stream()
                .map(v -> new FlashcardVertex(traversalSource, v))
                .toList();
    }

    public ExerciseSessionVertex getSession() {
        var vertexQuery = traversalSource.V(vertex).out(EDGE_SESSION);
        if (!vertexQuery.hasNext())
            return null;
        return new ExerciseSessionVertex(traversalSource, vertexQuery.next());
    }

    public void setSession(AbstractVertex session) {
        if (session == null)
            return;
        addEdgeOneToMany(EDGE_SESSION, session);
    }

    public static ExerciseVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseVertex(traversalSource, vertex);
    }

    public static ExerciseVertex getById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().has(LABEL, PROPERTY_ID, id).next();
        return new ExerciseVertex(traversalSource, vertex);
    }

    public static List<ExerciseVertex> getAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel(LABEL).toList();
        return vertices.stream()
                .map(v -> new ExerciseVertex(traversalSource, v))
                .toList();
    }

    public ExerciseModel toLimitedExerciseModel() {
        var model = new ExerciseModel();
        model.setId(getId());
        model.setType(getType());
        return model;
    }
}

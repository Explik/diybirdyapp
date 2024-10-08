package com.explik.diybirdyapp.graph.vertex;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class ExerciseVertex extends AbstractVertex {
    public ExerciseVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exercise";

    public final static String EDGE_CONTENT = "hasContent";
    public final static String EDGE_OPTION = "hasOption";
    public final static String EDGE_CORRECT_OPTION = "hasCorrectOption";

    public final static String PROPERTY_ID = "id";
    public final static String PROPERTY_TARGET_LANGUAGE = "targetLanguage";
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

    public void addOption(AbstractVertex option) {
        addEdgeOneToMany(EDGE_OPTION, option);
    }

    public List<? extends TextContentVertex> getOptions() {
        var choiceVertices = traversalSource.V(vertex).out(EDGE_OPTION).toList();

        return choiceVertices.stream()
                .map(v -> new TextContentVertex(traversalSource, v))
                .toList();
    }

    public TextContentVertex getCorrectOption() {
        var correctChoiceVertex = traversalSource.V(vertex).out(EDGE_CORRECT_OPTION).next();
        return new TextContentVertex(traversalSource, correctChoiceVertex);
    }

    public void setCorrectOption(AbstractVertex option) {
        addEdgeOneToOne(EDGE_CORRECT_OPTION, option);
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

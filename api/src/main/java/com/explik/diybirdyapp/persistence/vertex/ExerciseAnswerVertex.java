package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ExerciseAnswerVertex extends AbstractVertex {
    public ExerciseAnswerVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseAnswer";

    public final static String EDGE_EXERCISE = "hasExercise";
    public final static String EDGE_CONTENT = "hasContent";

    public final String PROPERTY_ID = "id";
    public final String PROPERTY_TYPE = "type";

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

    public ExerciseVertex getExercise() {
        return VertexHelper.getOutgoingModel(this, EDGE_EXERCISE, ExerciseVertex::new);
    }

    public void setExercise(ExerciseVertex exerciseVertex) {
        addEdgeOneToOne(EDGE_EXERCISE, exerciseVertex);
    }

    public AbstractVertex getTextContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_CONTENT, TextContentVertex::new);
    }

    public AbstractVertex getFlashcardContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_CONTENT, FlashcardVertex::new);
    }

    public void setContent(AbstractVertex contentVertex) {
        addEdgeOneToOne(EDGE_CONTENT, contentVertex);
    }

    public static ExerciseAnswerVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseAnswerVertex(traversalSource, vertex);
    }
}

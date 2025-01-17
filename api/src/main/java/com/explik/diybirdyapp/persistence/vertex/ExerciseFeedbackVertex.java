package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ExerciseFeedbackVertex extends AbstractVertex {
    public ExerciseFeedbackVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseFeedback";

    public final static String EDGE_EXERCISE_ANSWER = "hasAnswer";

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

    public ExerciseAnswerVertex getAnswer() {
        return VertexHelper.getOutgoingModel(this, EDGE_EXERCISE_ANSWER, ExerciseAnswerVertex::new);
    }

    public void setAnswer(ExerciseAnswerVertex answerVertex) {
        addEdgeOneToOne(EDGE_EXERCISE_ANSWER, answerVertex);
    }
}

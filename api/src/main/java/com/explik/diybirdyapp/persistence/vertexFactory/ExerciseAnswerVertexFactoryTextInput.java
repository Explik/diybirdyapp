package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseInputTextModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseInputTypes.TEXT + ComponentTypes.VERTEX_FACTORY)
public class ExerciseAnswerVertexFactoryTextInput implements VertexFactory<ExerciseAnswerVertex, ExerciseInputModel> {
    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseInputModel genericExerciseInput) {
        if (genericExerciseInput == null)
            throw new RuntimeException("Exercise answer model is null");
        if (!(genericExerciseInput instanceof ExerciseInputTextModel))
            throw new RuntimeException("Exercise answer model is not an instance of ExerciseInputTextModel");

        var exerciseAnswer = (ExerciseInputTextModel) genericExerciseInput;

        var graphVertex = traversalSource.addV(ExerciseAnswerVertex.LABEL).next();
        var vertex = new ExerciseAnswerVertex(traversalSource, graphVertex);
        vertex.setId(exerciseAnswer.getId());
        vertex.setType(ExerciseInputTypes.TEXT);

        // TODO set text content
        return vertex;
    }
}

package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseTypes.MULTIPLE_CHOICE_TEXT + ComponentTypes.VERTEX_FACTORY)
public class ExerciseAnswerVertexFactoryMultipleChoiceText implements VertexFactory<ExerciseVertex, ExerciseInputModel> {

    @Override
    public ExerciseVertex create(GraphTraversalSource traversalSource, ExerciseInputModel genericExerciseInput) {
        if (genericExerciseInput == null)
            throw new RuntimeException("Exercise answer model is null");
        if (!(genericExerciseInput instanceof ExerciseInputMultipleChoiceTextModel))
            throw new RuntimeException("Exercise answer model is not an instance of ExerciseInputMultipleChoiceTextModel");

        var exerciseAnswer = (ExerciseInputMultipleChoiceTextModel) genericExerciseInput;

        var graphVertex = traversalSource.addV(ExerciseVertex.LABEL).next();
        var vertex = new ExerciseVertex(traversalSource, graphVertex);
        vertex.setId(exerciseAnswer.getId());
        vertex.setType(ExerciseInputTypes.MULTIPLE_CHOICE);

        return vertex;
    }
}

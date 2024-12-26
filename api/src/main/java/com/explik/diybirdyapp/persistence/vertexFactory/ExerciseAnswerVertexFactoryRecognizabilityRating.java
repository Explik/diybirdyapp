package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseInputRecognizabilityRatingModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseInputTypes.RECOGNIZABILITY_RATING + ComponentTypes.VERTEX_FACTORY)
public class ExerciseAnswerVertexFactoryRecognizabilityRating implements VertexFactory<ExerciseAnswerVertex, ExerciseInputModel> {

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseInputModel exerciseAnswerModel) {
        if (exerciseAnswerModel == null)
            throw new RuntimeException("Exercise answer model is null");
        if (!(exerciseAnswerModel instanceof ExerciseInputRecognizabilityRatingModel))
            throw new RuntimeException("Exercise answer model is not an instance of ExerciseInputModel");

        // Fetch associated exercise vertex
        var exerciseVertex = ExerciseVertex.getById(traversalSource, exerciseAnswerModel.getExerciseId());

        // Create exercise answer vertex
        var graphVertex = traversalSource.addV(ExerciseAnswerVertex.LABEL).next();
        var vertex = new ExerciseAnswerVertex(traversalSource, graphVertex);
        vertex.setId(exerciseAnswerModel.getId());
        vertex.setType(ExerciseInputTypes.RECOGNIZABILITY_RATING);
        vertex.setExercise(exerciseVertex);

        return vertex;
    }
}

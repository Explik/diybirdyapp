package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseAnswerTypes;
import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseAnswerTypes.RECOGNIZABILITY_RATING + ComponentTypes.VERTEX_FACTORY)
public class ExerciseAnswerVertexFactoryRecognizabilityRating implements VertexFactory<ExerciseAnswerVertex, ExerciseAnswerModel> {

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseAnswerModel exerciseAnswerModel) {
        // Fetch associated exercise vertex
        var exerciseVertex = ExerciseVertex.getById(traversalSource, exerciseAnswerModel.getExerciseId());

        // Create exercise answer vertex
        var graphVertex = traversalSource.addV(ExerciseAnswerVertex.LABEL).next();
        var vertex = new ExerciseAnswerVertex(traversalSource, graphVertex);
        vertex.setId(exerciseAnswerModel.getId());
        vertex.setType(ExerciseAnswerTypes.RECOGNIZABILITY_RATING);
        vertex.setExercise(exerciseVertex);

        return vertex;
    }
}

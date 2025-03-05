package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseInputRecognizabilityRatingModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseAnswerVertexFactoryRecognizabilityRating implements VertexFactory<ExerciseAnswerVertex, ExerciseInputRecognizabilityRatingModel> {
    @Autowired
    private RecognizabilityRatingVertexFactory ratingVertexFactory;

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseInputRecognizabilityRatingModel answerModel) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());

        var ratingVertex = ratingVertexFactory.create(
                traversalSource,
                new RecognizabilityRatingVertexFactory.Options(UUID.randomUUID().toString(), answerModel.getRating()));

        var answerId = (answerModel.getId() != null) ? answerModel.getId() : java.util.UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        answerVertex.setRecognizabilityRating(ratingVertex);

        return answerVertex;
    }
}

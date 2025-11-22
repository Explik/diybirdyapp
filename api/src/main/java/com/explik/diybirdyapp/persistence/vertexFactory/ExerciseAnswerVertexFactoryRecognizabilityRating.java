package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.dto.exercise.ExerciseInputSelectReviewOptionsDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseAnswerVertexFactoryRecognizabilityRating implements VertexFactory<ExerciseAnswerVertex, ExerciseAnswerModel<ExerciseInputSelectReviewOptionsDto>> {
    @Autowired
    private RecognizabilityRatingVertexFactory ratingVertexFactory;

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseAnswerModel<ExerciseInputSelectReviewOptionsDto> answerModel) {
        var answerInput = answerModel.getInput();

        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answerModel.getSessionId());

        var ratingVertex = ratingVertexFactory.create(
                traversalSource,
                new RecognizabilityRatingVertexFactory.Options(UUID.randomUUID().toString(), answerInput.getRating()));

        var answerId = (answerInput.getId() != null) ? answerInput.getId() : java.util.UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        answerVertex.setSession(sessionVertex);
        answerVertex.setRecognizabilityRating(ratingVertex);

        return answerVertex;
    }
}

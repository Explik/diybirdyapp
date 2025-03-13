package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseInputTextModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseAnswerVertexFactoryText implements VertexFactory<ExerciseAnswerVertex, ExerciseInputTextModel> {
    @Autowired
    private TextContentVertexFactory textContentVertexFactory;

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseInputTextModel answerModel) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answerModel.getSessionId());

        var textContent = exerciseVertex.getTextContent();

        var textVertex = textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options(UUID.randomUUID().toString(), answerModel.getText(), textContent.getLanguage()));

        var answerId = (answerModel.getId() != null) ? answerModel.getId() : UUID.randomUUID().toString();
        var exerciseAnswerVertex = ExerciseAnswerVertex.create(traversalSource);
        exerciseAnswerVertex.setId(answerId);
        exerciseAnswerVertex.setExercise(exerciseVertex);
        exerciseAnswerVertex.setSession(sessionVertex);
        exerciseAnswerVertex.setContent(textVertex);

        return exerciseAnswerVertex;
    }
}

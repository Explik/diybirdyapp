package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseInputAudioModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseAnswerVertexFactoryAudio implements VertexFactory<ExerciseAnswerVertex, ExerciseInputAudioModel> {
    @Autowired
    AudioContentVertexFactory audioContentVertexFactory;

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseInputAudioModel answerModel) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());
        var flashcardContent = exerciseVertex.getFlashcardContent();
        var textContent = (TextContentVertex)flashcardContent.getLeftContent();
        var language = textContent.getLanguage();

        var audioVertex = audioContentVertexFactory.create(
                traversalSource,
                new AudioContentVertexFactory.Options(UUID.randomUUID().toString(), answerModel.getUrl(), language));

        var answerId = (answerModel.getId() != null) ? answerModel.getId() : UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        answerVertex.setContent(audioVertex);

        return answerVertex;
    }
}

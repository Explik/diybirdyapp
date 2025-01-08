package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.ExerciseInputAudioModel;
import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.AudioContentVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseTypes.PRONOUNCE_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseOperationsPronounceFlashcard implements ExerciseOperations {
    @Autowired
    private AudioContentVertexFactory audioContentVertexFactory;

    @Override
    public ExerciseModel evaluate(GraphTraversalSource traversalSource, ExerciseInputModel genericAnswerModel) {
        if (genericAnswerModel == null)
            throw new RuntimeException("Answer model is null");
        if (!(genericAnswerModel instanceof ExerciseInputAudioModel))
            throw new RuntimeException("Answer model type is not audio");


        ExerciseInputAudioModel answerModel = (ExerciseInputAudioModel) genericAnswerModel;
        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());
        var flashcardContent = exerciseVertex.getFlashcardContent();
        var flashcardSide = !exerciseVertex.getFlashcardSide().equals("front") ? flashcardContent.getLeftContent() : flashcardContent.getRightContent();
        var language = flashcardSide.getLanguage();

        // Save answer
        var answerId = (answerModel.getId() != null) ? answerModel.getId() : UUID.randomUUID().toString();
        var answerVertex = audioContentVertexFactory.create(
                traversalSource,
                new AudioContentVertexFactory.Options(answerId, answerModel.getUrl(), language));
        exerciseVertex.setAnswer(answerVertex);

        // Generate feedback
        var exerciseFeedback = ExerciseFeedbackModel.createIndecisiveFeedback();

        var exercise = new ExerciseModel();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getType());
        exercise.setAnswerId(answerVertex.getId());
        exercise.setFeedback(exerciseFeedback);

        return exercise;
    }
}

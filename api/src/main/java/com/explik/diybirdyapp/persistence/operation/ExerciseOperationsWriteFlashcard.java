package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputTextModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.TextContentVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component(ExerciseTypes.WRITE_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseOperationsWriteFlashcard implements ExerciseOperations {
    @Autowired
    private TextContentVertexFactory textContentVertexFactory;

    @Override
    public ExerciseModel evaluate(GraphTraversalSource traversalSource, ExerciseInputModel genericAnswerModel) {
        if (genericAnswerModel == null)
            throw new RuntimeException("Answer model is null");
        if (!(genericAnswerModel instanceof ExerciseInputTextModel))
            throw new RuntimeException("Answer model type is ExerciseInputTextModel");

        // Evaluate answer
        ExerciseInputTextModel answerModel = (ExerciseInputTextModel)genericAnswerModel;
        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());
        var flashcardContent = exerciseVertex.getFlashcardContent();
        var flashcardSide = (TextContentVertex)(!exerciseVertex.getFlashcardSide().equals("front") ? flashcardContent.getLeftContent() : flashcardContent.getRightContent());

        // Save answer
        var answerId = (answerModel.getId() != null) ? answerModel.getId() : UUID.randomUUID().toString();
        var answerVertex = textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options(answerId, answerModel.getText(), flashcardSide.getLanguage()));
        exerciseVertex.setAnswer(answerVertex);

        // Generate feedback
        return createExerciseWithFeedback(exerciseVertex, flashcardSide, answerModel);
    }

    private static ExerciseModel createExerciseWithFeedback(ExerciseVertex exerciseVertex, TextContentVertex flashcardSide, ExerciseInputTextModel answerModel) {

        var isAnswerCorrect = flashcardSide.getValue().equalsIgnoreCase(answerModel.getText());
        var exerciseFeedback = ExerciseFeedbackModel.createCorrectFeedback(isAnswerCorrect);
        exerciseFeedback.setMessage("Answer submitted successfully");

        var inputFeedback = new ExerciseInputTextModel.Feedback();
        inputFeedback.setCorrectValues(List.of(flashcardSide.getValue()));
        if (!isAnswerCorrect) inputFeedback.setIncorrectValues(List.of(answerModel.getText()));

        var input = new ExerciseInputTextModel();
        input.setFeedback(inputFeedback);

        var exercise = new ExerciseModel();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getType());
        exercise.setFeedback(exerciseFeedback);
        exercise.setInput(input);
        return exercise;
    }
}

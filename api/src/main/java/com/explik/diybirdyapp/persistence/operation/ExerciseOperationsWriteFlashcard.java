package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseAnswerTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAnswerVertexFactoryTextInput;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.WRITE_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseOperationsWriteFlashcard implements ExerciseOperations {
    @Autowired
    private ExerciseAnswerVertexFactoryTextInput answerVertexFactory;

    @Override
    public ExerciseModel evaluate(GraphTraversalSource traversalSource, ExerciseAnswerModel answerModel) {
        if (answerModel == null)
            throw new RuntimeException("Answer model is null");
        if (!answerModel.getType().equals(ExerciseAnswerTypes.TEXT))
            throw new RuntimeException("Answer model type is not write flashcard");

        answerVertexFactory.create(traversalSource, answerModel);

        // Generate feedback
        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());
        var flashcardContent = exerciseVertex.getFlashcardContent();
        var flashcardSide = exerciseVertex.getFlashcardSide().equals("front") ? flashcardContent.getLeftContent() : flashcardContent.getRightContent();
        var isAnswerCorrect = flashcardSide.getValue().equalsIgnoreCase(answerModel.getTextInput());

        var feedback = new ExerciseFeedbackModel();
        feedback.setType("general");
        feedback.setState(isAnswerCorrect ? "success" : "failure");
        feedback.setMessage("Answer submitted successfully");

        var exercise = new ExerciseModel();
        exercise.setFeedback(feedback);
        return exercise;
    }
}

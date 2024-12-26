package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAnswerVertexFactoryMultipleChoiceText;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseTypes.SELECT_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseOperationsSelectFlashcard implements ExerciseOperations {
    @Autowired
    ExerciseAnswerVertexFactoryMultipleChoiceText answerVertexFactory;

    @Override
    public ExerciseModel evaluate(GraphTraversalSource traversalSource, ExerciseInputModel genericAnswerModel) {
        if (genericAnswerModel == null)
            throw new RuntimeException("Answer model is null");
        if (!(genericAnswerModel instanceof ExerciseInputMultipleChoiceTextModel))
            throw new RuntimeException("Answer model type is not ExerciseInputMultipleChoiceTextModel");

        var answerModel = (ExerciseInputMultipleChoiceTextModel)genericAnswerModel;

        // Save answer to graph
        answerVertexFactory.create(traversalSource, answerModel);

        // Generate feedback
        // TODO Implement correct/incorrect feedback
        var exerciseFeedback = ExerciseFeedbackModel.createIndecisiveFeedback();
        exerciseFeedback.setMessage("Answer submitted successfully");

        var inputFeedback = new ExerciseInputMultipleChoiceTextModel.Feedback();
        inputFeedback.setCorrectOptionIds(List.of("id1"));
        inputFeedback.setIncorrectOptionIds(List.of("id2", "id3", "id4"));

        var exerciseInput = new ExerciseInputMultipleChoiceTextModel();
        exerciseInput.setFeedback(inputFeedback);

        var exercise = new ExerciseModel();
        exercise.setFeedback(exerciseFeedback);
        exercise.setInput(exerciseInput);

        return exercise;
    }
}

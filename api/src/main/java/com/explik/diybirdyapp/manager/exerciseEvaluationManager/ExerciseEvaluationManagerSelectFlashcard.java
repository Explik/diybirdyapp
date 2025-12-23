package com.explik.diybirdyapp.manager.exerciseEvaluationManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputSelectOptionsDto;
import com.explik.diybirdyapp.persistence.query.GetOptionsForExerciseQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.OptionsForExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseEvaluationTypes.CORRECT_OPTIONS + ComponentTypes.STRATEGY)
public class ExerciseEvaluationManagerSelectFlashcard implements ExerciseEvaluationManager {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private QueryHandler<GetOptionsForExerciseQuery, OptionsForExerciseModel> getOptionsForExerciseQueryHandler;

    @Override
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput() instanceof ExerciseInputSelectOptionsDto answerModel))
            throw new RuntimeException("Answer model type is not ExerciseInputMultipleChoiceTextModel");

        // Fetch all options using query
        var query = new GetOptionsForExerciseQuery();
        query.setExerciseId(exerciseVertex.getId());
        var options = getOptionsForExerciseQueryHandler.handle(query);

        // Validate selected option
        var selectedOptionId = answerModel.getValue();
        if (!options.getAllOptionIds().contains(selectedOptionId))
            throw new RuntimeException("Selected option not found");

        // Save answer
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answerModel.getSessionId());
        var selectedOptionVertex = ContentVertex.getById(traversalSource, selectedOptionId);

        var exerciseAnswerVertex = ExerciseAnswerVertex.create(traversalSource);
        exerciseAnswerVertex.setExercise(exerciseVertex);
        exerciseAnswerVertex.setSession(sessionVertex);
        exerciseAnswerVertex.setContent(selectedOptionVertex);

        // Generate feedback
        return createExerciseWithFeedback(options, answerModel, context);
    }

    private static ExerciseDto createExerciseWithFeedback(OptionsForExerciseModel options, ExerciseInputSelectOptionsDto answerModel, ExerciseEvaluationContext context) {
        var correctOptionId = options.getCorrectOptionId();
        var incorrectOptionIds = options.getIncorrectOptionIds();
        var isCorrect = answerModel.getValue().equals(correctOptionId);

        var exerciseFeedback = ExerciseFeedbackHelper.createCorrectFeedback(isCorrect);
        exerciseFeedback.setMessage("Answer submitted successfully");

        var inputFeedback = new ExerciseInputSelectOptionsDto.SelectOptionsInputFeedbackDto();
        inputFeedback.setCorrectOptionIds(List.of(correctOptionId));
        inputFeedback.setIncorrectOptionIds(incorrectOptionIds);

        if (context.getRetypeCorrectAnswerEnabled())
            inputFeedback.setIsRetypeAnswerEnabled(!isCorrect);

        var exerciseInput = new ExerciseInputSelectOptionsDto();
        exerciseInput.setFeedback(inputFeedback);

        var exercise = new ExerciseDto();
        exercise.setId(context.getExerciseId());
        //exercise.setType(ExerciseTypes.SELECT_FLASHCARD);
        exercise.setFeedback(exerciseFeedback);
        exercise.setInput(exerciseInput);
        return exercise;
    }
}

package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.manager.exerciseCreationManager.ExerciseCreationContext;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseSessionManager;
import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerSkippedCommand;
import com.explik.diybirdyapp.persistence.command.CreateExerciseFeedbackCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseAnswerCommandHelper;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.query.GetExerciseSessionByIdQuery;
import com.explik.diybirdyapp.persistence.query.GetExerciseSessionConfigQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.service.helper.ExerciseSessionConfigHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseSessionService {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private QueryHandler<GetExerciseSessionByIdQuery, ExerciseSessionDto> getExerciseSessionByIdQueryHandler;

    @Autowired
    private QueryHandler<GetExerciseSessionConfigQuery, ExerciseSessionOptionsDto> getExerciseSessionConfigQueryHandler;

    @Autowired
    private GenericProvider<ExerciseSessionManager> sessionOperationProvider;

    @Autowired
    private ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    private ExerciseSessionConfigHelper configHelper;

    @Autowired
    private CommandHandler<CreateExerciseAnswerSkippedCommand> createExerciseAnswerSkippedCommandHandler;

    @Autowired
    private CommandHandler<CreateExerciseFeedbackCommand> createExerciseFeedbackCommandHandler;

    public ExerciseSessionDto add(ExerciseSessionDto model) {
        var sessionType = model.getType();
        
        var sessionManager = sessionOperationProvider.get(sessionType);
        if (sessionManager == null)
            throw new IllegalArgumentException("No session manager found for type " + sessionType);

        return sessionManager.init(traversalSource, ExerciseCreationContext.createDefault(model));
    }

    public ExerciseSessionDto get(String id) {
        var query = new GetExerciseSessionByIdQuery();
        query.setId(id);
        return getExerciseSessionByIdQueryHandler.handle(query);
    }

    public ExerciseSessionDto nextExercise(String modelId) {
        var sessionVertex = getSessionVertex(modelId);
        var sessionType = sessionVertex.getType();
        var sessionManager = sessionOperationProvider.get(sessionType);

        var context = ExerciseCreationContext.createDefault(sessionModelFactory.create(sessionVertex));

        return sessionManager.nextExercise(traversalSource, context);
    }

    public ExerciseSessionDto skipExercise(String modelId) {
        var sessionVertex = getSessionVertex(modelId);
        var currentExercise = sessionVertex.getCurrentExercise();

        if (currentExercise != null) {
            var answerId = UUID.randomUUID().toString();

            var answerCommand = new CreateExerciseAnswerSkippedCommand();
            answerCommand.setId(answerId);
            answerCommand.setExerciseId(currentExercise.getId());
            answerCommand.setSessionId(sessionVertex.getId());
            createExerciseAnswerSkippedCommandHandler.handle(answerCommand);

            var feedbackCommand = new CreateExerciseFeedbackCommand();
            feedbackCommand.setExerciseAnswerId(answerId);
            feedbackCommand.setType(ExerciseAnswerCommandHelper.ANSWER_TYPE_SKIPPED);
            feedbackCommand.setStatus("correct");
            createExerciseFeedbackCommandHandler.handle(feedbackCommand);
        }

        return nextExercise(modelId);
    }

    public ExerciseSessionOptionsDto getConfig(String modelId) {
        var query = new GetExerciseSessionConfigQuery();
        query.setSessionId(modelId);
        return getExerciseSessionConfigQueryHandler.handle(query);
    }

    public ExerciseSessionDto updateConfig(String modelId, ExerciseSessionOptionsDto config) {
        return configHelper.updateConfig(modelId, config);
    }

    private ExerciseSessionVertex getSessionVertex(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);
        return sessionVertex;
    }
}

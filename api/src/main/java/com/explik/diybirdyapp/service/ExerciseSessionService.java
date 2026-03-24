package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.manager.exerciseCreationManager.ExerciseCreationContext;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseSessionManager;
import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerSkippedCommand;
import com.explik.diybirdyapp.persistence.command.CreateExerciseFeedbackCommand;
import com.explik.diybirdyapp.persistence.command.CompleteExerciseSessionCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseAnswerCommandHelper;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.query.GetExerciseSessionByIdQuery;
import com.explik.diybirdyapp.persistence.query.GetExerciseSessionConfigQuery;
import com.explik.diybirdyapp.persistence.query.GetUncompletedMatchingExerciseSessionQuery;
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
    private QueryHandler<GetUncompletedMatchingExerciseSessionQuery, ExerciseSessionDto> getUncompletedMatchingExerciseSessionQueryHandler;

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

    @Autowired
    private CommandHandler<CompleteExerciseSessionCommand> completeExerciseSessionCommandHandler;

    public ExerciseSessionDto getOrCreate(ExerciseSessionDto model) {
        var existingSession = findMatchingUncompletedSession(model);
        if (existingSession != null)
            return existingSession;

        var sessionType = model.getType();
        
        var sessionManager = sessionOperationProvider.get(sessionType);
        if (sessionManager == null)
            throw new IllegalArgumentException("No session manager found for type " + sessionType);

        return sessionManager.init(traversalSource, ExerciseCreationContext.createDefault(model));
    }

    private ExerciseSessionDto findMatchingUncompletedSession(ExerciseSessionDto model) {
        var query = new GetUncompletedMatchingExerciseSessionQuery();
        query.setType(model.getType());
        query.setFlashcardDeckId(model.getFlashcardDeckId());
        return getUncompletedMatchingExerciseSessionQueryHandler.handle(query);
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

    public ExerciseSessionDto restartSession(String modelId) {
        var sessionVertex = getSessionVertex(modelId);
        var originalConfig = getConfig(modelId);
        var sessionType = sessionVertex.getType();
        var flashcardDeck = sessionVertex.getFlashcardDeck();

        if (flashcardDeck == null)
            throw new IllegalArgumentException("No flashcard deck attached to session " + modelId);

        var completeCommand = new CompleteExerciseSessionCommand();
        completeCommand.setSessionId(modelId);
        completeExerciseSessionCommandHandler.handle(completeCommand);

        var sessionManager = sessionOperationProvider.get(sessionType);
        if (sessionManager == null)
            throw new IllegalArgumentException("No session manager found for type " + sessionType);

        var restartModel = new ExerciseSessionDto();
        restartModel.setType(sessionType);
        restartModel.setFlashcardDeckId(flashcardDeck.getId());

        var restartedSession = sessionManager.init(traversalSource, ExerciseCreationContext.createDefault(restartModel));

        if (originalConfig != null)
            return configHelper.updateConfig(restartedSession.getId(), originalConfig);

        return restartedSession;
    }

    private ExerciseSessionVertex getSessionVertex(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);
        return sessionVertex;
    }
}

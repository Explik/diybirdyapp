package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.CreateReviewFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseSessionTypes.REVIEW_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionManagerReviewFlashcardDeck implements ExerciseSessionManager {
    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Autowired
    private CommandHandler<CreateReviewFlashcardSessionCommand> createReviewFlashcardSessionCommandHandler;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Override
    public ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var options = context.getSessionModel();

        // Create session using command
        var sessionId = (options.getId() != null) ? options.getId() : UUID.randomUUID().toString();
        var command = new CreateReviewFlashcardSessionCommand();
        command.setId(sessionId);
        command.setFlashcardDeckId(options.getFlashcardDeckId());
        command.setTextToSpeechEnabled(false);
        command.setAlgorithm("SuperMemo2");
        
        createReviewFlashcardSessionCommandHandler.handle(command);

        // Load the created session
        var vertex = ExerciseSessionVertex.findById(traversalSource, sessionId);

        // Generate first exercise
        nextExerciseVertex(traversalSource, vertex);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var modelId = context.getSessionModel().getId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");

        // Generate next exercise
        nextExerciseVertex(traversalSource, sessionVertex);
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }

    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        // Finds first flashcard (in deck) not connected to review exercise (in session)
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.REVIEW_FLASHCARD);
        if (flashcardVertex != null)
            return createReviewExercise(traversalSource, sessionVertex, flashcardVertex);

        // Finds next flashcard according to spaced repetition algorithm
        var states = sessionVertex.getStatesWithType("SuperMemo2");
        var firstDueState = states.stream()
                .sorted((a, b) -> {
                    var nextShowA = (long)a.getPropertyValue("next_show");
                    var nextShowB = (long)b.getPropertyValue("next_show");
                    return Long.compare(nextShowA, nextShowB);
                })
                .findFirst();
        var firstDueFlashcard = (FlashcardVertex) firstDueState.map(state -> state.getContent()).orElse(null);
        return createReviewExercise(traversalSource, sessionVertex, firstDueFlashcard);
    }

    private ExerciseVertex createReviewExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex, FlashcardVertex flashcardVertex) {
        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(flashcardVertex));
        
        var command = exerciseCreationService.createExerciseCommand(ExerciseSchemas.REVIEW_FLASHCARD_EXERCISE, exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }
}

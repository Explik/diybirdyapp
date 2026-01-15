package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseSessionManager;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.command.CreateLearnFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component(ExerciseSessionTypes.LEARN_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionManagerLearnFlashcardDeck implements ExerciseSessionManager {
    @Autowired
    private FlashcardDeckExerciseManager exerciseManager;

    @Autowired
    private ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    private CommandHandler<CreateLearnFlashcardSessionCommand> createLearnFlashcardSessionCommandHandler;

    @Override
    public ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var options = context.getSessionModel();

        // Create session using command
        var sessionId = (options.getId() != null) ? options.getId() : UUID.randomUUID().toString();
        var command = new CreateLearnFlashcardSessionCommand();
        command.setId(sessionId);
        command.setFlashcardDeckId(options.getFlashcardDeckId());
        command.setRetypeCorrectAnswer(false);
        command.setTextToSpeechEnabled(false);
        command.setIncludeReviewExercises(true);
        command.setIncludeMultipleChoiceExercises(true);
        command.setIncludeWritingExercises(true);
        command.setIncludeListeningExercises(false);
        command.setIncludePronunciationExercises(true);
        
        var exerciseTypeIds = getInitialExerciseTypes(traversalSource).stream()
                .map(ExerciseTypeVertex::getId)
                .toList();
        command.setExerciseTypeIds(exerciseTypeIds);
        
        createLearnFlashcardSessionCommandHandler.handle(command);

        // Load the created session
        var vertex = ExerciseSessionVertex.findById(traversalSource, sessionId);

        // Generate first exercise
        exerciseManager.nextExerciseVertex(traversalSource, vertex);
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
        var exerciseVertex = exerciseManager.nextExerciseVertex(traversalSource, sessionVertex);
        
        // Mark session as completed if no more exercises
        if (exerciseVertex == null) {
            sessionVertex.setCompleted(true);
        }
        
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }

    private List<ExerciseTypeVertex> getInitialExerciseTypes(GraphTraversalSource traversalSource) {
        // This list should not contain any non-flashcard-based exercises as it breaks the next-exercise algorithm
        return List.of(
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.REVIEW_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.SELECT_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.WRITE_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.PRONOUNCE_FLASHCARD)
        );
    }
}

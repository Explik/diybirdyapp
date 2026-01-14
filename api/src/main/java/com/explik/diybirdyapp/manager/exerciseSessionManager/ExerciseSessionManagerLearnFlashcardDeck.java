package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
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
    private ReviewFlashcardExerciseCreationManager reviewFlashcardExerciseCreationManager;

    @Autowired
    private SelectFlashcardExerciseCreationManager selectFlashcardExerciseCreationManager;

    @Autowired
    private WriteFlashcardExerciseCreationManager writeFlashcardExerciseCreationManager;

    @Autowired
    private ListenAndSelectExerciseCreationManager listenAndSelectExerciseCreationManager;

    @Autowired
    private ListenAndWriteExerciseCreationManager listenAndWriteExerciseCreationManager;

    @Autowired
    private PronounceFlashcardExerciseCreationManager pronounceFlashcardExerciseCreationManager;

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

    // TODO Use flashcard side specified from session options
    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        FlashcardVertex flashcardVertex;
        var exerciseTypes = sessionVertex.getOptions().getExerciseTypes().stream()
                .map(ExerciseTypeVertex::getId)
                .toList();

        if (exerciseTypes.contains(ExerciseTypes.REVIEW_FLASHCARD)) {
            var reviewExerciseVertex = tryGenerateReviewExercise(traversalSource, sessionVertex);
            if (reviewExerciseVertex != null)
                return reviewExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.SELECT_FLASHCARD)) {
            var selectExerciseVertex = tryGenerateSelectExercise(traversalSource, sessionVertex);
            if (selectExerciseVertex != null)
                return selectExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.LISTEN_AND_SELECT)) {
            var listenAndSelectExerciseVertex = tryGenerateListenAndSelectExercise(traversalSource, sessionVertex);
            if (listenAndSelectExerciseVertex != null)
                return listenAndSelectExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.WRITE_FLASHCARD)) {
            var writeExerciseVertex = tryGenerateWriteExercise(traversalSource, sessionVertex);
            if (writeExerciseVertex != null)
                return writeExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.LISTEN_AND_WRITE)) {
            var listenAndWriteExerciseVertex = tryGenerateListenAndWriteExercise(traversalSource, sessionVertex);
            if (listenAndWriteExerciseVertex != null)
                return listenAndWriteExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.PRONOUNCE_FLASHCARD)) {
            var pronounceExerciseVertex = tryGeneratePronounceExercise(traversalSource, sessionVertex);
            if (pronounceExerciseVertex != null)
                return pronounceExerciseVertex;
        }

        // If no flashcards are found, the session is complete
        sessionVertex.setCompleted(true);
        return null;
    }

    private ExerciseVertex tryGenerateReviewExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.REVIEW_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                null,
                ExerciseTypes.REVIEW_FLASHCARD);
        
        return reviewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGenerateSelectExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.SELECT_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.SELECT_FLASHCARD);
        
        return selectFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGenerateListenAndSelectExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.LISTEN_AND_SELECT);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.LISTEN_AND_SELECT);
        
        return listenAndSelectExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGenerateWriteExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.WRITE_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.WRITE_FLASHCARD);
        
        return writeFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGenerateListenAndWriteExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.LISTEN_AND_WRITE);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.LISTEN_AND_WRITE);
        
        return listenAndWriteExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGeneratePronounceExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.PRONOUNCE_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.PRONOUNCE_FLASHCARD);
        
        return pronounceFlashcardExerciseCreationManager.createExercise(traversalSource, context);
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

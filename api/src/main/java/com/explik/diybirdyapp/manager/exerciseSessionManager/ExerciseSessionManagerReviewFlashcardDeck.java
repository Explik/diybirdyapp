package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.exerciseCreationManager.ExerciseCreationContext;
import com.explik.diybirdyapp.manager.exerciseCreationManager.ReviewFlashcardExerciseCreationManager;
import com.explik.diybirdyapp.manager.exerciseCreationManager.SortFlashcardExerciseCreationManager;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsReviewFlashcardsDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.command.CreateReviewFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseSessionTypes.REVIEW_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionManagerReviewFlashcardDeck implements ExerciseSessionManager {
    @Autowired
    private ReviewFlashcardExerciseCreationManager reviewFlashcardExerciseCreationManager;

    @Autowired
    private SortFlashcardExerciseCreationManager sortFlashcardExerciseCreationManager;

    @Autowired
    private CommandHandler<CreateReviewFlashcardSessionCommand> createReviewFlashcardSessionCommandHandler;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Override
    public ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var sessionModel = context.getSessionModel();

        // Resolve algorithm from options (default to SuperMemo2)
        String algorithm = "SuperMemo2";
        if (sessionModel.getOptions() instanceof ExerciseSessionOptionsReviewFlashcardsDto reviewOpts
                && reviewOpts.getAlgorithm() != null
                && !reviewOpts.getAlgorithm().isBlank()) {
            algorithm = reviewOpts.getAlgorithm();
        }

        // Create session using command
        var sessionId = (sessionModel.getId() != null) ? sessionModel.getId() : UUID.randomUUID().toString();
        var command = new CreateReviewFlashcardSessionCommand();
        command.setId(sessionId);
        command.setFlashcardDeckId(sessionModel.getFlashcardDeckId());
        command.setTextToSpeechEnabled(false);
        command.setAlgorithm(algorithm);

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
        var algorithm = sessionVertex.getOptions() != null ? sessionVertex.getOptions().getAlgorithm() : "SuperMemo2";

        if ("SimpleSort".equals(algorithm)) {
            return nextExerciseVertexSimpleSort(traversalSource, sessionVertex);
        } else {
            return nextExerciseVertexSuperMemo2(traversalSource, sessionVertex);
        }
    }

    private ExerciseVertex nextExerciseVertexSimpleSort(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        // First, show all flashcards that have not yet been sorted
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.SORT_FLASHCARD);
        if (flashcardVertex != null)
            return createSortExercise(traversalSource, sessionVertex, flashcardVertex);

        // Then, show flashcards the user put in the "dont-know" pile
        var states = sessionVertex.getStatesWithType("SimpleSort");
        var dontKnowFlashcard = states.stream()
                .filter(state -> "dont-know".equals(state.getPropertyValue("pile")))
                .map(state -> (FlashcardVertex) state.getContent())
                .findFirst()
                .orElse(null);

        // All cards are in the "know" pile — session is complete
        if (dontKnowFlashcard == null) {
            sessionVertex.setCompleted(true);
            return null;
        }

        return createSortExercise(traversalSource, sessionVertex, dontKnowFlashcard);
    }

    private ExerciseVertex nextExerciseVertexSuperMemo2(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
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
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                null,
                ExerciseTypes.REVIEW_FLASHCARD);

        return reviewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex createSortExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex, FlashcardVertex flashcardVertex) {
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                null,
                ExerciseTypes.SORT_FLASHCARD);

        return sortFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }
}


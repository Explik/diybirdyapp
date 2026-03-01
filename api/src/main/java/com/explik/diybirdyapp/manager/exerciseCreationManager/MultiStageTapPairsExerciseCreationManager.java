package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseInputParametersPairOptions;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Creates a multi-stage tap-pairs exercise for a session.
 * This is a session-level exercise using all text-text flashcard pairs in the active content batch.
 * Minimum of 8 text-text flashcard pairs is required to generate this exercise.
 */
@Component
public class MultiStageTapPairsExerciseCreationManager {

    /** Minimum number of text-text flashcard pairs required to create this exercise. */
    public static final int MIN_FLASHCARD_PAIRS = 8;

    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    /**
     * Creates a multi-stage tap pairs exercise from the given flashcard list.
     * Only text-text flashcards (both sides are text content) are used as pairs.
     * Returns null if fewer than MIN_FLASHCARD_PAIRS text-text pairs are available.
     *
     * @param traversalSource The graph traversal source
     * @param sessionVertex   The exercise session vertex
     * @param flashcards      List of flashcard vertices to source pairs from
     * @return The created exercise vertex, or null if the minimum pair count is not met
     */
    public ExerciseVertex createExercise(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            List<FlashcardVertex> flashcards) {

        // Collect text-text pairs: both sides must be TextContentVertex
        var pairs = flashcards.stream()
                .filter(f -> f.getLeftContent() instanceof TextContentVertex
                          && f.getRightContent() instanceof TextContentVertex)
                .map(f -> List.of((ContentVertex) f.getLeftContent(), (ContentVertex) f.getRightContent()))
                .toList();

        if (pairs.size() < MIN_FLASHCARD_PAIRS) {
            return null;
        }

        var pairParams = new ExerciseInputParametersPairOptions().withPairs(pairs);

        var exerciseParams = new ExerciseParameters()
                .withSession(sessionVertex)
                .withPairOptionsInput(pairParams);

        var command = exerciseCreationService.createExerciseCommand(
                ExerciseSchemas.MULTI_STAGE_TAP_PAIRS_EXERCISE,
                exerciseParams);
        createExerciseCommandHandler.handle(command);

        return ExerciseVertex.getById(traversalSource, exerciseParams.getId());
    }
}

package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseInputParametersPairOptions;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Creates a multi-stage tap-pairs exercise for a session.
 * This is a session-level exercise using text-text flashcard pairs in the active content batch.
 * Minimum of 8 text-text flashcard pairs is required to generate this exercise.
 */
@Component
public class MultiStageTapPairsExerciseCreationManager implements ExerciseCreationManager {

    /** Minimum number of text-text flashcard pairs required to create this exercise. */
    public static final int MIN_FLASHCARD_PAIRS = 8;

    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Override
    public ExerciseVertex createExercise(
            GraphTraversalSource traversalSource,
            ExerciseCreationContext context) {

        var sessionVertex = context.getSessionVertex();
        var contentStream = context.getContentStream();

        if (sessionVertex == null || contentStream == null) {
            return null;
        }

        // Collect text-text pairs lazily from the content stream.
        var pairs = contentStream
                .filter(vertex -> vertex instanceof FlashcardVertex)
                .map(vertex -> (FlashcardVertex) vertex)
                .filter(flashcard -> flashcard.getLeftContent() instanceof TextContentVertex
                        && flashcard.getRightContent() instanceof TextContentVertex)
                .map(flashcard -> java.util.List.of(
                        (ContentVertex) flashcard.getLeftContent(),
                        (ContentVertex) flashcard.getRightContent()))
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

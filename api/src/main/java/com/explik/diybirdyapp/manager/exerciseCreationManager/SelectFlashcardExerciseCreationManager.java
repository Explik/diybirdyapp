package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseInputParametersSelectOptions;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates select flashcard exercises.
 * Shows one side of a flashcard and asks user to select the correct option from multiple choices.
 */
@Component
public class SelectFlashcardExerciseCreationManager implements ExerciseCreationManager {
    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Override
    public ExerciseVertex createExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var sessionVertex = context.getSessionVertex();
        var flashcardVertex = context.getFlashcardVertex();
        var flashcardSide = context.getFlashcardSide() != null ? context.getFlashcardSide() : "front";

        if (sessionVertex == null || flashcardVertex == null) {
            return null;
        }

        var answerContentType = flashcardVertex.getOtherSide(flashcardSide).getClass();
        var correctContentVertex = flashcardVertex.getOtherSide(flashcardSide);
        
        // Get alternative answers from active content instead of flashcard deck
        var activeContent = context.getActiveContent();
        if (activeContent == null || activeContent.isEmpty()) {
            return null;
        }
        
        var incorrectContentVertices = activeContent.stream()
                .filter(vertex -> vertex instanceof FlashcardVertex)
                .map(vertex -> (FlashcardVertex) vertex)
                .filter(flashcard -> !flashcard.getId().equals(flashcardVertex.getId()))
                .filter(flashcard -> flashcard.getOtherSide(flashcardSide).getClass() == answerContentType)
                .limit(3)
                .map(f -> f.getOtherSide(flashcardSide))
                .toList();
        
        // Need at least 3 incorrect options for a meaningful multiple-choice exercise
        if (incorrectContentVertices.size() < 3) {
            return null;
        }

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withFlashcardContent(flashcardVertex, flashcardSide))
                .withBasedOnContent(flashcardVertex)
                .withSelectOptionsInput(new ExerciseInputParametersSelectOptions()
                        .withCorrectOptions(List.of(correctContentVertex))
                        .withIncorrectOptions(incorrectContentVertices)
                );
        
        var command = exerciseCreationService.createExerciseCommand(
                ExerciseSchemas.SELECT_FLASHCARD_EXERCISE, 
                exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }
}

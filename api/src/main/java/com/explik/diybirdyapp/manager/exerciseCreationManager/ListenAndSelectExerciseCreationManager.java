package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseInputParametersSelectOptions;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates listen and select exercises.
 * Plays audio and asks user to select the correct text from multiple choices.
 */
@Component
public class ListenAndSelectExerciseCreationManager implements ExerciseCreationManager {
    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Autowired
    private PronunciationHelper pronunciationHelper;

    @Override
    public ExerciseVertex createExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var sessionVertex = context.getSessionVertex();
        var flashcardVertex = context.getFlashcardVertex();
        var flashcardSide = context.getFlashcardSide() != null ? context.getFlashcardSide() : "front";

        if (sessionVertex == null || flashcardVertex == null) {
            return null;
        }

        // Fetch content
        var correctContentVertex = flashcardVertex.getSide(flashcardSide);
        if (!(correctContentVertex instanceof TextContentVertex textContentVertex)) {
            return null;
        }

        var pronunciationAudioVertex = pronunciationHelper.tryFetchOrGeneratePronunciation(
                traversalSource, 
                textContentVertex);
        if (pronunciationAudioVertex == null) {
            return null;
        }

        // Fetch alternative answers
        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();
        var alternativeFlashcardVertices = flashcardDeckVertex.getFlashcards().stream()
                .filter(flashcard -> !flashcard.getId().equals(flashcardVertex.getId()))
                .filter(flashcard -> flashcard.getSide(flashcardSide).getClass() == correctContentVertex.getClass())
                .limit(3)
                .collect(Collectors.toList());
        
        var incorrectContentVertices = alternativeFlashcardVertices.stream()
                .map(f -> f.getSide(flashcardSide))
                .toList();

        // Create exercise
        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(pronunciationAudioVertex))
                .withSelectOptionsInput(new ExerciseInputParametersSelectOptions()
                        .withCorrectOptions(List.of(correctContentVertex))
                        .withIncorrectOptions(incorrectContentVertices)
                );
        
        var command = exerciseCreationService.createExerciseCommand(
                ExerciseSchemas.LISTEN_AND_SELECT_EXERCISE, 
                exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }
}

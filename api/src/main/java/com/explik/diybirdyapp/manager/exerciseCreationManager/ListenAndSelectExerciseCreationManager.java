package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseInputParametersSelectOptions;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
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
        var pronunciationVertex = context.getPronunciationVertex();

        if (sessionVertex == null || pronunciationVertex == null) {
            return null;
        }

        // Get the audio content from the pronunciation
        var pronunciationAudioVertex = pronunciationVertex.getAudioContent();
        if (pronunciationAudioVertex == null) {
            return null;
        }

        // Get the text content that this pronunciation is for
        var correctContentVertex = pronunciationVertex.getTextContent();
        if (correctContentVertex == null) {
            return null;
        }

        // Fetch alternative answers from the same deck
        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();
        var alternativeFlashcardVertices = flashcardDeckVertex.getFlashcards().stream()
                .filter(flashcard -> {
                    var leftContent = flashcard.getLeftContent();
                    var rightContent = flashcard.getRightContent();
                    return (leftContent instanceof TextContentVertex && !leftContent.getId().equals(correctContentVertex.getId())) ||
                           (rightContent instanceof TextContentVertex && !rightContent.getId().equals(correctContentVertex.getId()));
                })
                .limit(3)
                .collect(Collectors.toList());
        
        var incorrectContentVertices = alternativeFlashcardVertices.stream()
                .flatMap(f -> {
                    var contents = new java.util.ArrayList<ContentVertex>();
                    if (f.getLeftContent() instanceof TextContentVertex && !f.getLeftContent().getId().equals(correctContentVertex.getId())) {
                        contents.add(f.getLeftContent());
                    }
                    if (f.getRightContent() instanceof TextContentVertex && !f.getRightContent().getId().equals(correctContentVertex.getId())) {
                        contents.add(f.getRightContent());
                    }
                    return contents.stream();
                })
                .limit(3)
                .toList();

        // Create exercise
        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(pronunciationAudioVertex))
                .withBasedOnContent(pronunciationVertex)
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

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

        // Get language of the correct answer for filtering
        var correctLanguage = correctContentVertex.getLanguage();
        
        // Get alternative answers from active content instead of flashcard deck
        var activeContent = context.getActiveContent();
        if (activeContent == null || activeContent.isEmpty()) {
            return null;
        }
        
        var incorrectContentVertices = activeContent.stream()
                .filter(vertex -> vertex instanceof TextContentVertex)
                .map(vertex -> (TextContentVertex) vertex)
                .filter(text -> !text.getId().equals(correctContentVertex.getId()))
                // Filter by same language if available
                .filter(text -> correctLanguage == null || text.getLanguage() == null || 
                               text.getLanguage().getId().equals(correctLanguage.getId()))
                .limit(3)
                .map(vertex -> (ContentVertex) vertex)
                .toList();
        
        // Need at least 3 incorrect options for a meaningful multiple-choice exercise
        if (incorrectContentVertices.size() >= 3) {
            return null;
        }

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

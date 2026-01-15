package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseInputParametersRecordAudio;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Creates pronunciation exercises.
 * Shows text and asks user to record their pronunciation.
 */
@Component
public class PronounceFlashcardExerciseCreationManager implements ExerciseCreationManager {
    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Override
    public ExerciseVertex createExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var sessionVertex = context.getSessionVertex();
        var textContentVertex = context.getTextContentVertex();

        if (sessionVertex == null || textContentVertex == null) {
            return null;
        }

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(textContentVertex))
                .withBasedOnContent(textContentVertex)
                .withRecordAudioInput(new ExerciseInputParametersRecordAudio().withCorrectOption(textContentVertex));
        
        var command = exerciseCreationService.createExerciseCommand(
                ExerciseSchemas.PRONOUNCE_FLASHCARD_EXERCISE, 
                exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }
}

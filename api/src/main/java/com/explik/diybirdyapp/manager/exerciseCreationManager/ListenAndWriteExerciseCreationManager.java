package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseInputParametersWriteText;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Creates listen and write exercises.
 * Plays audio and asks user to type what they hear.
 */
@Component
public class ListenAndWriteExerciseCreationManager implements ExerciseCreationManager {
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

        // Get the text content that this pronunciation is for - this is the answer
        var answerContentVertex = pronunciationVertex.getTextContent();
        if (answerContentVertex == null) {
            return null;
        }
        
        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(pronunciationAudioVertex))
                .withBasedOnContent(pronunciationVertex)
                .withWriteTextInput(new ExerciseInputParametersWriteText().withCorrectOption(answerContentVertex));
        
        var command = exerciseCreationService.createExerciseCommand(
                ExerciseSchemas.LISTEN_AND_WRITE_EXERCISE, 
                exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }
}

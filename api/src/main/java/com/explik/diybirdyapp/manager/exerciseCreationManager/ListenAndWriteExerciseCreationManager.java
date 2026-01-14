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
        var flashcardVertex = context.getFlashcardVertex();
        var flashcardSide = context.getFlashcardSide() != null ? context.getFlashcardSide() : "front";

        if (sessionVertex == null || flashcardVertex == null) {
            return null;
        }

        var questionContentVertex = flashcardVertex.getSide(flashcardSide);
        if (!(questionContentVertex instanceof TextContentVertex textContentVertex)) {
            return null;
        }

        var pronunciationAudioVertex = pronunciationHelper.tryFetchOrGeneratePronunciation(
                traversalSource, 
                textContentVertex);
        if (pronunciationAudioVertex == null) {
            return null;
        }

        var answerContentVertex = flashcardVertex.getOtherSide(flashcardSide);
        
        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(pronunciationAudioVertex))
                .withWriteTextInput(new ExerciseInputParametersWriteText().withCorrectOption(answerContentVertex));
        
        var command = exerciseCreationService.createExerciseCommand(
                ExerciseSchemas.LISTEN_AND_WRITE_EXERCISE, 
                exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }
}

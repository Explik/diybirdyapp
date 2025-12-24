package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.persistence.command.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Handler for the domain-level CreateExerciseCommand.
 * Orchestrates the creation of a complete exercise with content, input, and session associations.
 */
@Component
public class CreateExerciseCommandHandler implements CommandHandler<CreateExerciseCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;
    
    @Autowired
    private CommandHandler<CreatePairVertexCommand> createPairVertexCommandHandler;

    @Override
    public void handle(CreateExerciseCommand command) {
        // Create the exercise vertex
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        var id = (command.getId() != null) ? command.getId() : UUID.randomUUID().toString();
        exerciseVertex.setId(id);

        // Set exercise type
        var exerciseTypeVertex = ExerciseTypeVertex.findById(traversalSource, command.getExerciseTypeId());
        if (exerciseTypeVertex == null) {
            exerciseTypeVertex = ExerciseTypeVertex.create(traversalSource);
            exerciseTypeVertex.setId(command.getExerciseTypeId());
        }
        exerciseVertex.setExerciseType(exerciseTypeVertex);

        // Attach to session if provided
        if (command.getSessionId() != null) {
            var sessionVertex = ExerciseSessionVertex.findById(traversalSource, command.getSessionId());
            if (sessionVertex != null) {
                sessionVertex.addExercise(exerciseVertex);
            }
        }

        // Set target language if provided
        if (command.getTargetLanguageId() != null) {
            exerciseVertex.setTargetLanguage(command.getTargetLanguageId());
        }

        // Attach content
        attachContent(exerciseVertex, command);

        // Attach input based on which input command is set
        if (command.getArrangeTextOptionsInput() != null) {
            attachArrangeTextOptionsInput(exerciseVertex, command.getArrangeTextOptionsInput());
        } else if (command.getSelectOptionsInput() != null) {
            attachSelectOptionsInput(exerciseVertex, command.getSelectOptionsInput());
        } else if (command.getPairOptionsInput() != null) {
            attachPairOptionsInput(exerciseVertex, command.getPairOptionsInput());
        } else if (command.getWriteTextInput() != null) {
            attachWriteTextInput(exerciseVertex, command.getWriteTextInput());
        } else if (command.getRecordAudioInput() != null) {
            attachRecordAudioInput(exerciseVertex, command.getRecordAudioInput());
        } else if (command.getRecognizabilityRatingInput() != null) {
            // Recognizability rating doesn't require input setup
        }
    }

    private void attachContent(ExerciseVertex exerciseVertex, CreateExerciseCommand command) {
        // Attach flashcard content with side
        if (command.getFlashcardId() != null && command.getFlashcardSide() != null) {
            var flashcardVertex = (FlashcardVertex) ContentVertex.getById(traversalSource, command.getFlashcardId());
            if (flashcardVertex == null) {
                throw new RuntimeException("Flashcard not found: " + command.getFlashcardId());
            }
            exerciseVertex.setFlashcardContent(flashcardVertex, command.getFlashcardSide());
        }
        // Attach generic content
        else if (command.getContentId() != null) {
            var contentVertex = ContentVertex.getById(traversalSource, command.getContentId());
            if (contentVertex == null) {
                throw new RuntimeException("Content not found: " + command.getContentId());
            }
            exerciseVertex.setContent(contentVertex);
        }
    }

    private void attachArrangeTextOptionsInput(ExerciseVertex exerciseVertex, CreateArrangeTextOptionsInputCommand input) {
        if (input.getOptionIds() != null) {
            for (var optionId : input.getOptionIds()) {
                var optionVertex = ContentVertex.getById(traversalSource, optionId);
                exerciseVertex.addOption(optionVertex);
                optionVertex.makeStatic();
            }
        }
    }

    private void attachSelectOptionsInput(ExerciseVertex exerciseVertex, CreateSelectOptionsInputCommand input) {
        // Add correct options
        if (input.getCorrectOptionIds() != null) {
            for (var optionId : input.getCorrectOptionIds()) {
                var optionVertex = ContentVertex.getById(traversalSource, optionId);
                exerciseVertex.addCorrectOption(optionVertex);
                optionVertex.makeStatic();
            }
        }
        
        // Add incorrect options
        if (input.getIncorrectOptionIds() != null) {
            for (var optionId : input.getIncorrectOptionIds()) {
                var optionVertex = ContentVertex.getById(traversalSource, optionId);
                exerciseVertex.addOption(optionVertex);
                optionVertex.makeStatic();
            }
        }
    }

    private void attachPairOptionsInput(ExerciseVertex exerciseVertex, CreatePairOptionsInputCommand input) {
        if (input.getPairs() != null) {
            for (var pairDef : input.getPairs()) {
                // Create the pair vertex
                var createPairCmd = new CreatePairVertexCommand();
                createPairCmd.setId(UUID.randomUUID().toString());
                createPairCmd.setLeft(pairDef.getLeftId());
                createPairCmd.setRight(pairDef.getRightId());
                createPairVertexCommandHandler.handle(createPairCmd);
                
                // Attach pair to exercise
                var pairVertex = PairVertex.findById(traversalSource, createPairCmd.getId());
                exerciseVertex.addOptionPair(pairVertex);
            }
        }
    }

    private void attachWriteTextInput(ExerciseVertex exerciseVertex, CreateWriteTextInputCommand input) {
        if (input.getCorrectOptionId() != null) {
            var optionVertex = ContentVertex.getById(traversalSource, input.getCorrectOptionId());
            exerciseVertex.addCorrectOption(optionVertex);
            // Don't make static for write text
        }
    }

    private void attachRecordAudioInput(ExerciseVertex exerciseVertex, CreateRecordAudioInputCommand input) {
        if (input.getCorrectOptionId() != null) {
            var optionVertex = ContentVertex.getById(traversalSource, input.getCorrectOptionId());
            exerciseVertex.addCorrectOption(optionVertex);
            // Don't make static for record audio
        }
    }
}

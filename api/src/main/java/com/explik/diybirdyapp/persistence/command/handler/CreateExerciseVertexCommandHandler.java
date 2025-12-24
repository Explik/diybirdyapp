package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateExerciseVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateExerciseVertexCommandHandler implements CommandHandler<CreateExerciseVertexCommand> {
    private final GraphTraversalSource traversalSource;
    private final CompositeCommandHandler compositeCommandHandler;

    public CreateExerciseVertexCommandHandler(
            @Autowired GraphTraversalSource traversalSource,
            @Autowired CompositeCommandHandler compositeCommandHandler) {
        this.traversalSource = traversalSource;
        this.compositeCommandHandler = compositeCommandHandler;
    }

    @Override
    public void handle(CreateExerciseVertexCommand command) {
        // Execute sub-commands (e.g., pair creation)
        compositeCommandHandler.handle(command);

        // Create exercise vertex
        var vertex = ExerciseVertex.create(traversalSource);
        var id = (command.getId() != null) ? command.getId() : UUID.randomUUID().toString();
        vertex.setId(id);

        // Set exercise type
        var exerciseTypeVertex = ExerciseTypeVertex.findById(traversalSource, command.getExerciseTypeId());
        if (exerciseTypeVertex == null) {
            exerciseTypeVertex = ExerciseTypeVertex.create(traversalSource);
            exerciseTypeVertex.setId(command.getExerciseTypeId());
        }
        vertex.setExerciseType(exerciseTypeVertex);

        // Attach to session if provided
        if (command.getSessionId() != null) {
            var sessionVertex = ExerciseSessionVertex.findById(traversalSource, command.getSessionId());
            if (sessionVertex != null) {
                sessionVertex.addExercise(vertex);
            }
        }

        // Set target language if provided
        if (command.getTargetLanguageId() != null) {
            var languageVertex = LanguageVertex.findById(traversalSource, command.getTargetLanguageId());
            vertex.setTargetLanguage(languageVertex.getId());
        }

        // Attach content
        if (command.getContentId() != null) {
            var contentVertex = ContentVertex.getById(traversalSource, command.getContentId());
            vertex.setContent(contentVertex);
        }

        // Attach flashcard content
        if (command.getFlashcardId() != null && command.getFlashcardSide() != null) {
            var flashcardVertex = (FlashcardVertex) ContentVertex.getById(traversalSource, command.getFlashcardId());
            vertex.setFlashcardContent(flashcardVertex, command.getFlashcardSide());
        }

        // Attach correct options
        if (command.getCorrectOptionIds() != null) {
            for (var optionId : command.getCorrectOptionIds()) {
                var optionVertex = ContentVertex.getById(traversalSource, optionId);
                vertex.addCorrectOption(optionVertex);
                optionVertex.makeStatic();
            }
        }

        // Attach incorrect options
        if (command.getIncorrectOptionIds() != null) {
            for (var optionId : command.getIncorrectOptionIds()) {
                var optionVertex = ContentVertex.getById(traversalSource, optionId);
                vertex.addOption(optionVertex);
                optionVertex.makeStatic();
            }
        }

        // Attach arrange text options
        if (command.getArrangeTextOptionIds() != null) {
            for (var optionId : command.getArrangeTextOptionIds()) {
                var optionVertex = ContentVertex.getById(traversalSource, optionId);
                vertex.addOption(optionVertex);
                optionVertex.makeStatic();
            }
        }

        // Attach pair options
        if (command.getPairCommands() != null) {
            for (var pairCommand : command.getPairCommands()) {
                var pairVertex = PairVertex.findById(traversalSource, pairCommand.getId());
                vertex.addOptionPair(pairVertex);
            }
        }
    }
}

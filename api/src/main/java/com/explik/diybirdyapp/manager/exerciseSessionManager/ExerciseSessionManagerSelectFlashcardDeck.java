package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseInputParametersSelectOptions;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component(ExerciseSessionTypes.SELECT_FLASHCARD_DECK + ComponentTypes.OPERATIONS)
public class ExerciseSessionManagerSelectFlashcardDeck implements ExerciseSessionManager {
    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Override
    public ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var options = context.getSessionModel();

        // Resolve neighboring vertices
        var flashcardDeckVertex = FlashcardDeckVertex.findById(traversalSource, options.getFlashcardDeckId());
        if (flashcardDeckVertex == null)
            throw new IllegalArgumentException("Flashcard deck with id" + options.getFlashcardDeckId() + "not found");
        if (flashcardDeckVertex.getFlashcards().isEmpty())
            throw new IllegalArgumentException("Flashcard deck with id" + options.getFlashcardDeckId() + "is empty");

        // Create the vertex
        var sessionId = options.getId() != null ? options.getId() : java.util.UUID.randomUUID().toString();
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setId(sessionId);
        vertex.setType(ExerciseSessionTypes.SELECT_FLASHCARD_DECK);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        var flashcardLanguages = flashcardDeckVertex.getFlashcardLanguages();
        var optionVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionVertex.setId(UUID.randomUUID().toString());
        optionVertex.setType(ExerciseSessionTypes.SELECT_FLASHCARD_DECK);
        optionVertex.setTextToSpeechEnabled(false);
        optionVertex.setInitialFlashcardLanguageId(flashcardLanguages.getFirst().getId());
        vertex.setOptions(optionVertex);

        // Generate first exercise
        nextExerciseVertex(traversalSource, vertex);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var sessionId = context.getSessionModel().getId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + sessionId +" not found");

        // Generate next exercise
        nextExerciseVertex(traversalSource, sessionVertex);
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }

    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        // Finds first flashcard (in deck) not connected to review exercise (in session)
        // TODO Add support for non-text flashcards
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.SELECT_FLASHCARD);

        if (flashcardVertex != null) {
            var flashcardSide = "front";
            var flashcardDeckVertex = sessionVertex.getFlashcardDeck();
            var answerContentType = flashcardVertex.getOtherSide(flashcardSide).getClass();
            var alternativeFlashcardVertices = flashcardDeckVertex.getFlashcards().stream()
                    .filter(flashcard -> !flashcard.getId().equals(flashcardVertex.getId())) // Skips the current flashcard
                    .filter(flashcard -> flashcard.getOtherSide(flashcardSide).getClass() == answerContentType) // Skips flashcards with different content type
                    .limit(3)
                    .collect(Collectors.toList());

            var correctContentVertex = flashcardVertex.getOtherSide(flashcardSide);
            var incorrectContentVertices = alternativeFlashcardVertices
                    .stream()
                    .map(f -> f.getOtherSide(flashcardSide))
                    .toList();

            var exerciseParameters = new ExerciseParameters()
                    .withSession(sessionVertex)
                    .withContent(new ExerciseContentParameters().withFlashcardContent(flashcardVertex, flashcardSide))
                    .withSelectOptionsInput(new ExerciseInputParametersSelectOptions()
                            .withCorrectOptions(List.of(correctContentVertex))
                            .withIncorrectOptions(incorrectContentVertices)
                    );
            
            var command = exerciseCreationService.createExerciseCommand(ExerciseSchemas.SELECT_FLASHCARD_EXERCISE, exerciseParameters);
            createExerciseCommandHandler.handle(command);
            
            var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
            return ExerciseVertex.getById(traversalSource, exerciseId);
        } else {
            // If no flashcards are found, the session is complete
            sessionVertex.setCompleted(true);
        }

        return null;
    }
}

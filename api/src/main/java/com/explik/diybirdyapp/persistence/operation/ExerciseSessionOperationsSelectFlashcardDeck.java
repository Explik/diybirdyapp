package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAbstractVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseInputParametersSelectOptions;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseParameters;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component(ExerciseSessionTypes.SELECT_FLASHCARD_DECK + ComponentTypes.OPERATIONS)
public class ExerciseSessionOperationsSelectFlashcardDeck implements ExerciseSessionOperations {
    @Autowired
    ExerciseAbstractVertexFactory abstractVertexFactory;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Override
    public ExerciseSessionModel init(GraphTraversalSource traversalSource, ExerciseSessionModel options) {
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

        var optionVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionVertex.setId(UUID.randomUUID().toString());
        optionVertex.setFlashcardSide("front"); // Start with this side
        vertex.setOptions(optionVertex);

        // Generate first exercise
        nextExercise(traversalSource, sessionId);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionModel nextExercise(GraphTraversalSource traversalSource, String sessionId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + sessionId +" not found");

        // Finds first flashcard (in deck) not connected to review exercise (in session)
        // TODO Add support for non-text flashcards
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionId, ExerciseTypes.SELECT_FLASHCARD);

        if (flashcardVertex != null) {
            var flashcardSide = sessionVertex.getOptions().getFlashcardSide();
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
            var exerciseVertexFactory = abstractVertexFactory.create(ExerciseSchemas.SELECT_FLASHCARD_EXERCISE);
            exerciseVertexFactory.create(traversalSource, exerciseParameters);

            sessionVertex.reload();
        } else {
            // If no flashcards are found, the session is complete
            sessionVertex.setCompleted(true);
        }
        return sessionModelFactory.create(sessionVertex);
    }
}

package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseModelFactorySelectFlashcard;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseSelectFlashcardVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component(ExerciseSessionTypes.SELECT_FLASHCARD_DECK + ComponentTypes.OPERATIONS)
public class ExerciseSessionOperationsSelectFlashcardDeck implements ExerciseSessionOperations {
    @Autowired
    ExerciseSelectFlashcardVertexFactory vertexFactory;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    ExerciseModelFactorySelectFlashcard exerciseModelFactory;

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
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionId, ExerciseTypes.SELECT_FLASHCARD);

        if (flashcardVertex != null) {
            var flashcardDeckVertex = sessionVertex.getFlashcardDeck();
            var alternativeFlashcardVertices = flashcardDeckVertex.getFlashcards().stream()
                    .filter(flashcard -> !flashcard.getId().equals(flashcardVertex.getId()))
                    .limit(3)
                    .collect(Collectors.toList());

            var flashcardSide = sessionVertex.getOptions().getFlashcardSide();
            var correctContentVertex = flashcardVertex.getSide(flashcardSide);
            var incorrectContentVertices = alternativeFlashcardVertices
                    .stream()
                    .map(f -> f.getOtherSide(flashcardSide))
                    .toList();

            vertexFactory.create(
                    traversalSource,
                    new ExerciseSelectFlashcardVertexFactory.Options(
                            UUID.randomUUID().toString(),
                            sessionVertex,
                            correctContentVertex,
                            incorrectContentVertices));
            sessionVertex.reload();
        } else {
            // If no flashcards are found, the session is complete
            sessionVertex.setCompleted(true);
        }
        return sessionModelFactory.create(sessionVertex);
    }
}

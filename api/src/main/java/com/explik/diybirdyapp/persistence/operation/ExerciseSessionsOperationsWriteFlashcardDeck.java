package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseModelFactoryWriteFlashcard;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseVertexFactoryWriteFlashcard;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseSessionTypes.WRITE_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionsOperationsWriteFlashcardDeck implements ExerciseSessionOperations{
    @Autowired
    ExerciseVertexFactoryWriteFlashcard vertexFactory;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    ExerciseModelFactoryWriteFlashcard exerciseModelFactory;

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
        vertex.setType(ExerciseSessionTypes.WRITE_FLASHCARD);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        var optionVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionVertex.setId(UUID.randomUUID().toString());
        optionVertex.setFlashcardSide("front");
        vertex.setOptions(optionVertex);

        // Generate first exercise
        nextExercise(traversalSource, sessionId);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionModel nextExercise(GraphTraversalSource traversalSource, String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");

        // Finds first flashcard (in deck) not connected to review exercise (in session)
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, modelId, ExerciseTypes.WRITE_FLASHCARD);

        if (flashcardVertex != null) {
            var flashcardSide = sessionVertex.getOptions().getFlashcardSide();
            var questionContentVertex = flashcardVertex.getSide(flashcardSide);
            var answerContentVertex = flashcardVertex.getOtherSide(flashcardSide);

            vertexFactory.create(
                    traversalSource,
                    new ExerciseVertexFactoryWriteFlashcard.Options(UUID.randomUUID().toString(), sessionVertex, questionContentVertex, answerContentVertex));
            sessionVertex.reload();
        }
        else {
            sessionVertex.setCompleted(true);
        }

        return sessionModelFactory.create(sessionVertex);
    }
}

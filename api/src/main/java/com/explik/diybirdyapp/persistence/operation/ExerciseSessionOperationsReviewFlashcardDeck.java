package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseReviewFlashcardVertexFactory;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseModelFactoryReviewFlashcard;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseSessionTypes.REVIEW_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionOperationsReviewFlashcardDeck implements ExerciseSessionOperations {
    @Autowired
    ExerciseReviewFlashcardVertexFactory vertexFactory;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    ExerciseModelFactoryReviewFlashcard exerciseModelFactory;

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

        var graphVertex = traversalSource.addV(ExerciseSessionVertex.LABEL).next();
        var vertex = new ExerciseSessionVertex(traversalSource, graphVertex);
        vertex.setId(sessionId);
        vertex.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        // Generate first exercise
        next(traversalSource, sessionId);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseModel next(GraphTraversalSource traversalSource, String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");

        // Finds first flashcard (in deck) not connected to review exercise (in session)
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, modelId, ExerciseTypes.REVIEW_FLASHCARD);
        if (flashcardVertex == null)
            return null; // No more flashcards to review

        var exerciseVertex = vertexFactory.create(
                traversalSource,
                new ExerciseReviewFlashcardVertexFactory.Options(UUID.randomUUID().toString(), sessionVertex, flashcardVertex));

        return exerciseModelFactory.create(exerciseVertex);
    }
}
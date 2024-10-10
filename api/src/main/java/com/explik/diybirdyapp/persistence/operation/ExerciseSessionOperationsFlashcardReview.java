package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseReviewFlashcardVertexFactory;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseReviewFlashcardModelFactory;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseSessionTypes.REVIEW_FLASHCARD + ComponentTypes.MANAGER)
public class ExerciseSessionOperationsFlashcardReview implements ExerciseSessionOperations {
    @Autowired
    ExerciseReviewFlashcardVertexFactory vertexFactory;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    ExerciseReviewFlashcardModelFactory exerciseModelFactory;

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
        var query = traversalSource.V()
                .has(ExerciseSessionVertex.LABEL, ExerciseSessionVertex.PROPERTY_ID, modelId)
                .out(ExerciseSessionVertex.EDGE_FLASHCARD_DECK)
                .out(FlashcardDeckVertex.EDGE_FLASHCARD)
                .not(__.in(ExerciseVertex.EDGE_CONTENT)
                        .out(ExerciseVertex.EDGE_SESSION)
                        .has(ExerciseSessionVertex.LABEL, ExerciseSessionVertex.PROPERTY_ID, modelId));

        if (!query.hasNext())
            return null; // No more flashcards to review

        var flashcardGraphVertex = query.next();
        var flashcardVertex = new FlashcardVertex(traversalSource, flashcardGraphVertex);

        var exerciseVertex = vertexFactory.create(
                traversalSource,
                new ExerciseReviewFlashcardVertexFactory.Options(modelId, sessionVertex, flashcardVertex));

        return exerciseModelFactory.create(exerciseVertex);
    }
}

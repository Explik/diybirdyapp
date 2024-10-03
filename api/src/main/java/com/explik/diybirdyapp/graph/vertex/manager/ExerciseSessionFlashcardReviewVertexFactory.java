package com.explik.diybirdyapp.graph.vertex.manager;

import com.explik.diybirdyapp.graph.model.ExerciseSessionModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import com.explik.diybirdyapp.graph.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.graph.vertex.FlashcardVertex;
import com.explik.diybirdyapp.graph.vertex.factory.ExerciseReviewFlashcardVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("flashcard-review")
public class ExerciseSessionFlashcardReviewVertexFactory implements ExerciseSessionManager {
    @Autowired
    ExerciseReviewFlashcardVertexFactory exerciseFactory;

    @Override
    public ExerciseSessionVertex init(GraphTraversalSource traversalSource, ExerciseSessionModel options) {
        // Resolve neighboring vertices
        var flashcardDeckVertex = FlashcardDeckVertex.findById(traversalSource, options.getFlashcardDeckId());
        if (flashcardDeckVertex == null)
            throw new IllegalArgumentException("Flashcard deck with id" + options.getFlashcardDeckId() + "not found");
        if (flashcardDeckVertex.getFlashcards().isEmpty())
            throw new IllegalArgumentException("Flashcard deck with id" + options.getFlashcardDeckId() + "is empty");

        // Create the vertex
        var graphVertex = traversalSource.addV(ExerciseSessionVertex.LABEL).next();
        var vertex = new ExerciseSessionVertex(traversalSource, graphVertex);
        vertex.setId(options.getId());
        vertex.setType("flashcard-review");
        vertex.setFlashcardDeck(flashcardDeckVertex);

        return vertex;
    }

    @Override
    public ExerciseVertex next(GraphTraversalSource traversalSource, String modelId) {
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

        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);

        return exerciseFactory.create(
                traversalSource,
                new ExerciseReviewFlashcardVertexFactory.Options(modelId, sessionVertex, flashcardVertex));
    }
}

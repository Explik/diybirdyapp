package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAbstractVertexFactory;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseParameters;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseSessionTypes.REVIEW_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionOperationsReviewFlashcardDeck implements ExerciseSessionOperations {
    @Autowired
    private ExerciseAbstractVertexFactory abstractVertexFactory;

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
        vertex.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        var flashcardLanguages = flashcardDeckVertex.getFlashcardLanguages();
        var optionVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionVertex.setId(UUID.randomUUID().toString());
        optionVertex.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        optionVertex.setTextToSpeechEnabled(false);
        optionVertex.setInitialFlashcardLanguageId(flashcardLanguages.getFirst().getId());
        optionVertex.setAlgorithm("SuperMemo2");
        vertex.setOptions(optionVertex);

        // Generate first exercise
        nextExerciseVertex(traversalSource, vertex);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var modelId = context.getSessionModel().getId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");

        // Generate next exercise
        nextExerciseVertex(traversalSource, sessionVertex);
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }

    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        // Finds first flashcard (in deck) not connected to review exercise (in session)
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.REVIEW_FLASHCARD);
        if (flashcardVertex != null)
            return createReviewExercise(traversalSource, sessionVertex, flashcardVertex);

        // Finds next flashcard according to spaced repetition algorithm
        var states = sessionVertex.getStatesWithType("SuperMemo2");
        var firstDueState = states.stream()
                .sorted((a, b) -> {
                    var nextShowA = (long)a.getPropertyValue("next_show");
                    var nextShowB = (long)b.getPropertyValue("next_show");
                    return Long.compare(nextShowA, nextShowB);
                })
                .findFirst();
        var firstDueFlashcard = (FlashcardVertex) firstDueState.map(state -> state.getContent()).orElse(null);
        return createReviewExercise(traversalSource, sessionVertex, firstDueFlashcard);
    }

    private ExerciseVertex createReviewExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex, FlashcardVertex flashcardVertex) {
        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(flashcardVertex));
        var exerciseFactory = abstractVertexFactory.create(ExerciseSchemas.REVIEW_FLASHCARD_EXERCISE);

        return exerciseFactory.create(traversalSource, exerciseParameters);
    }
}

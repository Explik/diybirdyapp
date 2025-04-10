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
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseInputParametersWriteText;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseParameters;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseSessionTypes.LEARN_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionOperationsLearnFlashcardDeck implements ExerciseSessionOperations {
    @Autowired
    private ExerciseAbstractVertexFactory abstractVertexFactory;

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

        // Create/init the session vertex
        var sessionId = options.getId() != null ? options.getId() : java.util.UUID.randomUUID().toString();
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setId(sessionId);
        vertex.setType(ExerciseSessionTypes.LEARN_FLASHCARD);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        var optionVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionVertex.setId(UUID.randomUUID().toString());
        optionVertex.setFlashcardSide("front");
        vertex.setOptions(optionVertex);

        // Generate first exercise
        nextExerciseVertex(traversalSource, vertex);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionModel nextExercise(GraphTraversalSource traversalSource, String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");

        // Generate next exercise
        nextExerciseVertex(traversalSource, sessionVertex);
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }

    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        FlashcardVertex flashcardVertex;

        flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.REVIEW_FLASHCARD);
        if (flashcardVertex != null) {
            var exerciseParameters = new ExerciseParameters()
                    .withSession(sessionVertex)
                    .withContent(new ExerciseContentParameters().withContent(flashcardVertex));
            var exerciseFactory = abstractVertexFactory.create(ExerciseSchemas.REVIEW_FLASHCARD_EXERCISE);

            return exerciseFactory.create(traversalSource, exerciseParameters);
        }

        flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.WRITE_FLASHCARD);
        if (flashcardVertex != null) {
            var flashcardSide = sessionVertex.getOptions().getFlashcardSide();
            var questionContentVertex = flashcardVertex.getSide(flashcardSide);
            var answerContentVertex = flashcardVertex.getOtherSide(flashcardSide);

            var exerciseParameters = new ExerciseParameters()
                    .withSession(sessionVertex)
                    .withContent(new ExerciseContentParameters().withFlashcardContent(flashcardVertex, flashcardSide))
                    .withWriteTextInput(new ExerciseInputParametersWriteText().withCorrectOption(answerContentVertex));
            var exerciseFactory = abstractVertexFactory.create(ExerciseSchemas.WRITE_FLASHCARD_EXERCISE);

            return exerciseFactory.create(traversalSource, exerciseParameters);
        }
        else {
            // If no flashcards are found, the session is complete
            sessionVertex.setCompleted(true);
        }

        return null;
    }
}

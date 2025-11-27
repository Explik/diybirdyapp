package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAbstractVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component(ExerciseSessionTypes.LEARN_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionOperationsLearnFlashcardDeck implements ExerciseSessionOperations {
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

        // Create/init the session vertex
        var sessionId = options.getId() != null ? options.getId() : java.util.UUID.randomUUID().toString();
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setId(sessionId);
        vertex.setType(ExerciseSessionTypes.LEARN_FLASHCARD);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        var flashcardLanguages = flashcardDeckVertex.getFlashcardLanguages();
        var optionVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionVertex.setId(UUID.randomUUID().toString());
        optionVertex.setType(ExerciseSessionTypes.LEARN_FLASHCARD);
        optionVertex.setRetypeCorrectAnswer(false);
        optionVertex.setTextToSpeechEnabled(false);
        for (var languageVertex : flashcardLanguages)
            optionVertex.addAnswerLanguage(languageVertex);
        for (var exerciseTypeVertex : getInitialExerciseTypes(traversalSource))
            optionVertex.addExerciseType(exerciseTypeVertex);

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

    // TODO Use flashcard side specified from session options
    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        FlashcardVertex flashcardVertex;
        var exerciseTypes = sessionVertex.getOptions().getExerciseTypes().stream()
                .map(ExerciseTypeVertex::getId)
                .toList();

        if (exerciseTypes.contains(ExerciseTypes.REVIEW_FLASHCARD)) {
            var reviewExerciseVertex = tryGenerateReviewExercise(traversalSource, sessionVertex);
            if (reviewExerciseVertex != null)
                return reviewExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.SELECT_FLASHCARD)) {
            var selectExerciseVertex = tryGenerateSelectExercise(traversalSource, sessionVertex);
            if (selectExerciseVertex != null)
                return selectExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.LISTEN_AND_SELECT)) {
            var listenAndSelectExerciseVertex = tryGenerateListenAndSelectExercise(traversalSource, sessionVertex);
            if (listenAndSelectExerciseVertex != null)
                return listenAndSelectExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.WRITE_FLASHCARD)) {
            var writeExerciseVertex = tryGenerateWriteExercise(traversalSource, sessionVertex);
            if (writeExerciseVertex != null)
                return writeExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.LISTEN_AND_WRITE)) {
            var listenAndWriteExerciseVertex = tryGenerateListenAndWriteExercise(traversalSource, sessionVertex);
            if (listenAndWriteExerciseVertex != null)
                return listenAndWriteExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.PRONOUNCE_FLASHCARD)) {
            var pronounceExerciseVertex = tryGeneratePronounceExercise(traversalSource, sessionVertex);
            if (pronounceExerciseVertex != null)
                return pronounceExerciseVertex;
        }

        // If no flashcards are found, the session is complete
        sessionVertex.setCompleted(true);
        return null;
    }

    private ExerciseVertex tryGenerateReviewExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.REVIEW_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(flashcardVertex));
        var exerciseFactory = abstractVertexFactory.create(ExerciseSchemas.REVIEW_FLASHCARD_EXERCISE);

        return exerciseFactory.create(traversalSource, exerciseParameters);
    }

    private ExerciseVertex tryGenerateSelectExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.SELECT_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var flashcardSide = "front";
        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();
        var answerContentType = flashcardVertex.getOtherSide(flashcardSide).getClass();
        FlashcardVertex finalFlashcardVertex = flashcardVertex;
        var alternativeFlashcardVertices = flashcardDeckVertex.getFlashcards().stream()
                .filter(flashcard -> !flashcard.getId().equals(finalFlashcardVertex.getId())) // Skips the current flashcard
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
        return exerciseVertexFactory.create(traversalSource, exerciseParameters);
    }

    private ExerciseVertex tryGenerateListenAndSelectExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        // TODO implement
        return null;
    }

    private ExerciseVertex tryGenerateWriteExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.WRITE_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var flashcardSide = "front";
        var questionContentVertex = flashcardVertex.getSide(flashcardSide);
        var answerContentVertex = flashcardVertex.getOtherSide(flashcardSide);

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withFlashcardContent(flashcardVertex, flashcardSide))
                .withWriteTextInput(new ExerciseInputParametersWriteText().withCorrectOption(answerContentVertex));
        var exerciseFactory = abstractVertexFactory.create(ExerciseSchemas.WRITE_FLASHCARD_EXERCISE);

        return exerciseFactory.create(traversalSource, exerciseParameters);
    }

    private ExerciseVertex tryGenerateListenAndWriteExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        // TODO implement
        return null;
    }

    private ExerciseVertex tryGeneratePronounceExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.PRONOUNCE_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var flashcardSide = "front";
        var flashcardContentVertex = flashcardVertex.getSide(flashcardSide);
        if (!(flashcardContentVertex instanceof TextContentVertex textContentVertex))
            return null;

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withFlashcardContent(flashcardVertex, flashcardSide))
                .withRecordAudioInput(new ExerciseInputParametersRecordAudio().withCorrectOption(textContentVertex));
        var exerciseVertexFactory = abstractVertexFactory.create(ExerciseSchemas.PRONOUNCE_FLASHCARD_EXERCISE);
        return exerciseVertexFactory.create(traversalSource, exerciseParameters);
    }

    private List<ExerciseTypeVertex> getInitialExerciseTypes(GraphTraversalSource traversalSource) {
        return List.of(
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.REVIEW_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.SELECT_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.WRITE_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.PRONOUNCE_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.LISTEN_AND_SELECT),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.LISTEN_AND_WRITE)
        );
    }
}

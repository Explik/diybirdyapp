package com.explik.diybirdyapp.persistence.command.helper;

import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.UUID;

/**
 * Helper class for creating ExerciseAnswerVertex with associated content.
 * This class provides shared functionality for creating exercise answer vertices
 * and linking them to exercises, sessions, and content.
 */
public class ExerciseAnswerCommandHelper {

    /**
     * Creates an ExerciseAnswerVertex with text content.
     *
     * @param traversalSource the graph traversal source
     * @param id the ID for the answer vertex (generated if null)
     * @param exerciseId the exercise ID
     * @param sessionId the session ID
     * @param text the text content
     * @return the created ExerciseAnswerVertex
     */
    public static ExerciseAnswerVertex createWithTextContent(
            GraphTraversalSource traversalSource,
            String id,
            String exerciseId,
            String sessionId,
            String text) {
        
        var exerciseVertex = ExerciseVertex.getById(traversalSource, exerciseId);
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        var languageVertex = extractLanguageFromExercise(exerciseVertex);

        // Create text content
        var textId = UUID.randomUUID().toString();
        var textVertex = TextContentVertex.create(traversalSource);
        textVertex.setId(textId);
        textVertex.setValue(text);
        textVertex.setLanguage(languageVertex);

        // Create and link answer vertex
        return createAnswerVertex(traversalSource, id, exerciseVertex, sessionVertex, textVertex);
    }

    /**
     * Creates an ExerciseAnswerVertex with audio content.
     *
     * @param traversalSource the graph traversal source
     * @param id the ID for the answer vertex (generated if null)
     * @param exerciseId the exercise ID
     * @param sessionId the session ID
     * @param audioUrl the audio URL
     * @return the created ExerciseAnswerVertex
     */
    public static ExerciseAnswerVertex createWithAudioContent(
            GraphTraversalSource traversalSource,
            String id,
            String exerciseId,
            String sessionId,
            String audioUrl) {
        
        var exerciseVertex = ExerciseVertex.getById(traversalSource, exerciseId);
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        var languageVertex = extractLanguageFromExercise(exerciseVertex);

        // Create audio content
        var audioId = UUID.randomUUID().toString();
        var audioVertex = AudioContentVertex.create(traversalSource);
        audioVertex.setId(audioId);
        audioVertex.setUrl(audioUrl);
        audioVertex.setLanguage(languageVertex);

        // Create and link answer vertex
        return createAnswerVertex(traversalSource, id, exerciseVertex, sessionVertex, audioVertex);
    }

    /**
     * Creates an ExerciseAnswerVertex with recognizability rating.
     *
     * @param traversalSource the graph traversal source
     * @param id the ID for the answer vertex (generated if null)
     * @param exerciseId the exercise ID
     * @param sessionId the session ID
     * @param rating the recognizability rating value
     * @return the created ExerciseAnswerVertex
     */
    public static ExerciseAnswerVertex createWithRecognizabilityRating(
            GraphTraversalSource traversalSource,
            String id,
            String exerciseId,
            String sessionId,
            String rating) {
        
        var exerciseVertex = ExerciseVertex.getById(traversalSource, exerciseId);
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);

        // Create recognizability rating
        var ratingId = UUID.randomUUID().toString();
        var ratingVertex = RecognizabilityRatingVertex.create(traversalSource);
        ratingVertex.setId(ratingId);
        ratingVertex.setRating(rating);

        // Create answer vertex
        var answerId = (id != null) ? id : UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        answerVertex.setSession(sessionVertex);
        answerVertex.setRecognizabilityRating(ratingVertex);

        return answerVertex;
    }

    /**
     * Creates an ExerciseAnswerVertex and links it to exercise, session, and content.
     *
     * @param traversalSource the graph traversal source
     * @param id the ID for the answer vertex (generated if null)
     * @param exerciseVertex the exercise vertex
     * @param sessionVertex the session vertex
     * @param contentVertex the content vertex
     * @return the created ExerciseAnswerVertex
     */
    private static ExerciseAnswerVertex createAnswerVertex(
            GraphTraversalSource traversalSource,
            String id,
            ExerciseVertex exerciseVertex,
            ExerciseSessionVertex sessionVertex,
            ContentVertex contentVertex) {
        
        var answerId = (id != null) ? id : UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        answerVertex.setSession(sessionVertex);
        answerVertex.setContent(contentVertex);

        return answerVertex;
    }

    /**
     * Extracts the language from an exercise's content.
     * Handles both direct TextContent and FlashcardContent.
     *
     * @param exerciseVertex the exercise vertex
     * @return the language vertex
     * @throws RuntimeException if content type is unsupported
     */
    private static LanguageVertex extractLanguageFromExercise(ExerciseVertex exerciseVertex) {
        var content = exerciseVertex.getContent();
        
        if (content instanceof TextContentVertex textContentVertex) {
            return textContentVertex.getLanguage();
        }
        else if (content instanceof FlashcardVertex flashcardVertex) {
            var flashcardSide = exerciseVertex.getFlashcardSide();
            var textContentVertex = (TextContentVertex) flashcardVertex.getSide(flashcardSide);
            return textContentVertex.getLanguage();
        }
        else if (content instanceof AudioContentVertex audioContentVertex) {
            return audioContentVertex.getLanguage();
        }
        
        throw new RuntimeException("Unsupported content type for exercise: " + content.getClass().getSimpleName());
    }
}

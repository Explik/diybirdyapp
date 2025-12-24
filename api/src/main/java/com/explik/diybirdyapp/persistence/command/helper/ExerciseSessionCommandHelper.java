package com.explik.diybirdyapp.persistence.command.helper;

import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.List;
import java.util.UUID;

/**
 * Helper class for creating exercise session vertices.
 * This class provides shared functionality for creating exercise session vertices
 * and their options vertices without relying on other command handlers.
 */
public class ExerciseSessionCommandHelper {

    /**
     * Creates and initializes a basic exercise session vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param sessionId the session ID (generates one if null)
     * @param sessionType the type of the session
     * @param flashcardDeckId the flashcard deck ID
     * @return the created ExerciseSessionVertex
     * @throws IllegalArgumentException if the flashcard deck is not found or is empty
     */
    public static ExerciseSessionVertex createSessionVertex(
            GraphTraversalSource traversalSource,
            String sessionId,
            String sessionType,
            String flashcardDeckId) {
        
        // Resolve neighboring vertices
        var flashcardDeckVertex = FlashcardDeckVertex.findById(traversalSource, flashcardDeckId);
        if (flashcardDeckVertex == null)
            throw new IllegalArgumentException("Flashcard deck with id " + flashcardDeckId + " not found");
        if (flashcardDeckVertex.getFlashcards().isEmpty())
            throw new IllegalArgumentException("Flashcard deck with id " + flashcardDeckId + " is empty");

        // Create the session vertex
        var id = sessionId != null ? sessionId : UUID.randomUUID().toString();
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setId(id);
        vertex.setType(sessionType);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        return vertex;
    }

    /**
     * Creates a basic exercise session options vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param sessionType the type of the session
     * @param textToSpeechEnabled whether text-to-speech is enabled
     * @return the created ExerciseSessionOptionsVertex
     */
    public static ExerciseSessionOptionsVertex createOptionsVertex(
            GraphTraversalSource traversalSource,
            String sessionType,
            Boolean textToSpeechEnabled) {
        
        var optionVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionVertex.setId(UUID.randomUUID().toString());
        optionVertex.setType(sessionType);
        optionVertex.setTextToSpeechEnabled(textToSpeechEnabled != null ? textToSpeechEnabled : false);
        
        return optionVertex;
    }

    /**
     * Sets the initial flashcard language on the options vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param optionVertex the options vertex
     * @param initialFlashcardLanguageId the initial language ID (uses first language if null)
     * @param flashcardDeckVertex the flashcard deck vertex
     */
    public static void setInitialFlashcardLanguage(
            GraphTraversalSource traversalSource,
            ExerciseSessionOptionsVertex optionVertex,
            String initialFlashcardLanguageId,
            FlashcardDeckVertex flashcardDeckVertex) {
        
        var flashcardLanguages = flashcardDeckVertex.getFlashcardLanguages();
        if (initialFlashcardLanguageId != null) {
            optionVertex.setInitialFlashcardLanguageId(initialFlashcardLanguageId);
        } else if (!flashcardLanguages.isEmpty()) {
            optionVertex.setInitialFlashcardLanguageId(flashcardLanguages.getFirst().getId());
        }
    }

    /**
     * Adds answer languages to the options vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param optionVertex the options vertex
     * @param answerLanguageIds the answer language IDs (uses first language if null/empty)
     * @param flashcardDeckVertex the flashcard deck vertex
     */
    public static void addAnswerLanguages(
            GraphTraversalSource traversalSource,
            ExerciseSessionOptionsVertex optionVertex,
            List<String> answerLanguageIds,
            FlashcardDeckVertex flashcardDeckVertex) {
        
        var flashcardLanguages = flashcardDeckVertex.getFlashcardLanguages();
        
        if (answerLanguageIds != null && !answerLanguageIds.isEmpty()) {
            for (String languageId : answerLanguageIds) {
                var languageVertex = LanguageVertex.findById(traversalSource, languageId);
                if (languageVertex != null) {
                    optionVertex.addAnswerLanguage(languageVertex);
                }
            }
        } else if (!flashcardLanguages.isEmpty()) {
            optionVertex.addAnswerLanguage(flashcardLanguages.getFirst());
        }
    }
}

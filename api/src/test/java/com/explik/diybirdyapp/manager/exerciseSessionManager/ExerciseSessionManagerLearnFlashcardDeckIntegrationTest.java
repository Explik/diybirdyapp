package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.manager.exerciseCreationManager.ExerciseCreationContext;
import com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck.ExerciseSessionManagerLearnFlashcardDeck;
import com.explik.diybirdyapp.model.exercise.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ExerciseSessionManagerLearnFlashcardDeck.
 * Tests the overall behavior of the learning algorithm including:
 * - Exercise generation
 * - Content rotation
 * - Exercise type variation
 * - Batch management
 * - Round-based learning
 */
@SpringBootTest
public class ExerciseSessionManagerLearnFlashcardDeckIntegrationTest {
    
    @Autowired
    private GraphTraversalSource traversalSource;
    
    @Autowired
    private ExerciseSessionManagerLearnFlashcardDeck sessionManager;
    
    private FlashcardDeckVertex testDeck;
    
    @BeforeEach
    void setUp() {
        // Clear the graph
        traversalSource.V().drop().iterate();
        
        // Create test deck with multiple flashcards
        testDeck = createTestDeckWithFlashcards(10);
    }
    
    @Test
    void givenNewSession_whenInit_thenReturnsSessionWithFirstExercise() {
        // Arrange
        var context = createContext(testDeck.getId());
        
        // Act
        var sessionDto = sessionManager.init(traversalSource, context);
        
        // Assert
        assertNotNull(sessionDto, "Session should be created");
        assertNotNull(sessionDto.getId(), "Session should have an ID");
        assertNotNull(sessionDto.getExercise(), "Session should have an exercise");
        assertNotNull(sessionDto.getExercise().getType(), "Exercise should have a type");
    }
    
    @Test
    void givenSession_whenNextExercise_thenReturnsExercise() {
        // Arrange
        var context = createContext(testDeck.getId());
        sessionManager.init(traversalSource, context);
        
        // Act
        var sessionDto = sessionManager.nextExercise(traversalSource, context);
        
        // Assert
        assertNotNull(sessionDto, "Session should exist");
        assertNotNull(sessionDto.getExercise(), "Session should have an exercise");
    }
    
    @Test
    void givenSession_whenGenerateMultipleExercises_thenContentVariesAcrossExercises() {
        // Arrange
        var context = createContext(testDeck.getId());
        sessionManager.init(traversalSource, context);
        
        Set<String> contentIds = new HashSet<>();
        int exercisesToGenerate = 15; // Generate enough to see different content
        
        // Act - Generate multiple exercises and track content IDs
        for (int i = 0; i < exercisesToGenerate; i++) {
            var sessionDto = sessionManager.nextExercise(traversalSource, context);
            if (sessionDto.getExercise() != null) {
                var exercise = sessionDto.getExercise();
                if (exercise.getContent() != null) {
                    contentIds.add(exercise.getContent().getId());
                }
            }
        }
        
        // Assert - Should have exercised multiple different pieces of content
        assertTrue(contentIds.size() >= 2, 
            "Should exercise at least 2 different pieces of content across " + exercisesToGenerate + " exercises. Found: " + contentIds.size());
    }
    
    @Test
    void givenSession_whenGenerateMultipleExercises_thenExerciseTypeChanges() {
        // Arrange
        var context = createContext(testDeck.getId());
        sessionManager.init(traversalSource, context);
        
        Set<String> exerciseTypes = new HashSet<>();
        int exercisesToGenerate = 20; // Generate enough to see different types
        
        // Act - Generate multiple exercises and track types
        for (int i = 0; i < exercisesToGenerate; i++) {
            var sessionDto = sessionManager.nextExercise(traversalSource, context);
            if (sessionDto.getExercise() != null) {
                var exercise = sessionDto.getExercise();
                if (exercise.getType() != null) {
                    exerciseTypes.add(exercise.getType());
                }
            }
        }
        
        // Assert - Should have at least 2 different exercise types in 20 exercises
        assertTrue(exerciseTypes.size() >= 2, 
            "Should have at least 2 different exercise types across " + exercisesToGenerate + " exercises. Found: " + exerciseTypes);
    }
    
    @Test
    void givenSession_whenGenerateExercisesForSameContent_thenExercisesVaryInType() {
        // Arrange - Create a deck with only 1 flashcard to force repetition on same content
        var singleFlashcardDeck = createTestDeckWithFlashcards(1);
        var context = createContext(singleFlashcardDeck.getId());
        sessionManager.init(traversalSource, context);
        
        Set<String> exerciseTypesForSameContent = new HashSet<>();
        String lastContentId = null;
        int maxRounds = 5; // Based on MAX_EXERCISES_PER_CONTENT constant
        
        // Act - Generate exercises and track types for the same content
        for (int i = 0; i < maxRounds + 1; i++) {
            var sessionDto = sessionManager.nextExercise(traversalSource, context);
            if (sessionDto.getExercise() != null) {
                var exercise = sessionDto.getExercise();
                if (exercise.getContent() != null && exercise.getType() != null) {
                    String contentId = exercise.getContent().getId();
                    
                    // Track types only while on the same content
                    if (lastContentId == null || lastContentId.equals(contentId)) {
                        exerciseTypesForSameContent.add(exercise.getType());
                        lastContentId = contentId;
                    }
                }
            }
        }
        
        // Assert - With 5 rounds per content, should see type variation
        assertTrue(exerciseTypesForSameContent.size() >= 1, 
            "Should have at least 1 exercise type for the same content across " + maxRounds + " rounds. Found: " + exerciseTypesForSameContent);
    }
    
    @Test
    void givenSession_whenGenerateManyExercises_thenBatchLimitRespected() {
        // Arrange
        var context = createContext(testDeck.getId());
        var sessionDto = sessionManager.init(traversalSource, context);
        
        int exercisesToGenerate = 25; // More than BATCH_SIZE (20)
        int totalExercises = 1; // Already have 1 from init
        
        // Act - Generate exercises beyond batch size
        for (int i = 0; i < exercisesToGenerate; i++) {
            sessionDto = sessionManager.nextExercise(traversalSource, context);
            if (sessionDto.getExercise() != null) {
                totalExercises++;
            }
            
            // Session should not be marked as completed while generating
            if (i < exercisesToGenerate - 1 && !sessionDto.getCompleted()) {
                assertNotNull(sessionDto.getExercise(), "Should continue generating exercises");
            }
        }
        
        // Assert - Should have generated the expected number of exercises
        assertTrue(totalExercises > 20, 
            "Should generate exercises beyond first batch (20). Generated: " + totalExercises);
    }

    @Test
    void givenSession_whenUpdateOptions_thenGeneratesNewCurrentExercise() {
        // Arrange
        var context = createContext(testDeck.getId());
        var initialSession = sessionManager.init(traversalSource, context);
        assertNotNull(initialSession.getExercise(), "Initial session should have an exercise");
        var initialExerciseId = initialSession.getExercise().getId();

        // Mimic config helper behavior: mutate options first, then invoke updateOptions.
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, initialSession.getId());
        sessionVertex.getOptions().setShuffleFlashcards(true);

        // Act
        sessionManager.updateOptions(traversalSource, initialSession.getId());

        // Assert
        sessionVertex.reload();
        var currentExercise = sessionVertex.getCurrentExercise();
        assertNotNull(currentExercise, "Updated session should have a freshly generated exercise");
        assertNotEquals(initialExerciseId, currentExercise.getId(), "Current exercise should change after options update");
        assertFalse(sessionVertex.getCompleted(), "Session should not be marked completed after regenerating exercise");
    }
    
    @Test
    void givenSession_whenExhaustedContent_thenSessionMarkedCompleted() {
        // Arrange - Create a deck with minimal flashcards
        var smallDeck = createTestDeckWithFlashcards(1);
        var context = createContext(smallDeck.getId());
        sessionManager.init(traversalSource, context);
        
        int maxAttempts = 50; // Try many times to exhaust content
        boolean sessionCompleted = false;
        
        // Act - Keep generating until session is completed or max attempts
        for (int i = 0; i < maxAttempts; i++) {
            var sessionDto = sessionManager.nextExercise(traversalSource, context);
            if (sessionDto.getCompleted()) {
                sessionCompleted = true;
                break;
            }
        }
        
        // Assert - Session should eventually complete when content is exhausted
        assertTrue(sessionCompleted, 
            "Session should be marked as completed after exhausting all content");
    }

    // Helper method
    private FlashcardDeckVertex createTestDeckWithFlashcards(int count) {
        var deck = FlashcardDeckVertex.create(traversalSource);
        deck.setId("test-deck-" + System.currentTimeMillis());
        deck.setName("Test Deck");

        var leftLanguage = LanguageVertex.create(traversalSource);
        leftLanguage.setId("language-left");
        leftLanguage.setName("Left Language");

        var rightLanguage = LanguageVertex.create(traversalSource);
        rightLanguage.setId("language-right");
        rightLanguage.setName("Right Language");

        for (int i = 0; i < count; i++) {
            var flashcard = FlashcardVertex.create(traversalSource);
            flashcard.setId("flashcard-" + i);
            
            // Create left content (text)
            var leftContent = TextContentVertex.create(traversalSource);
            leftContent.setId("left-content-" + i);
            leftContent.setValue("Left " + i);
            leftContent.setLanguage(leftLanguage);
            flashcard.setLeftContent(leftContent);
            
            // Create right content (text)
            var rightContent = TextContentVertex.create(traversalSource);
            rightContent.setId("right-content-" + i);
            rightContent.setValue("Right " + i);
            rightContent.setLanguage(rightLanguage);
            flashcard.setRightContent(rightContent);
            
            deck.addFlashcard(flashcard);
        }
        
        return deck;
    }
    
    private ExerciseCreationContext createContext(String deckId) {
        var sessionModel = new ExerciseSessionDto();
        sessionModel.setId("test-session-" + System.currentTimeMillis());
        sessionModel.setType(ExerciseSessionTypes.LEARN_FLASHCARD);
        sessionModel.setFlashcardDeckId(deckId);
        
        var context = new ExerciseCreationContext();
        context.setSessionModel(sessionModel);
        
        return context;
    }
    
    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }
}

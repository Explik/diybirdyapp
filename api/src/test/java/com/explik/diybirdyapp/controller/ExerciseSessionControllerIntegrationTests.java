package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsLearnFlashcardsDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.service.DataInitializerService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ExerciseSessionControllerIntegrationTests {
    @Autowired
    DataInitializerService dataInitializerService;

    @Autowired
    ExerciseSessionController controller;

    @Autowired
    GraphTraversalSource traversalSource;

    @BeforeEach
    void setUp() {
        dataInitializerService.resetInitialData();
    }

    @Test
    void givenNothing_whenCreate_thenReturnExerciseSession() {
        var session = new ExerciseSessionDto();
        session.setId("new-id");
        session.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        session.setFlashcardDeckId("flashcardDeckVertex2");

        var savedSession = controller.create(session);

        assertNotNull(savedSession);
        assertNotNull(savedSession.getExercise());
        assertEquals(session.getId(), savedSession.getId());
        assertEquals(session.getType(), savedSession.getType());
    }

    @Test
    void givenMatchingUncompletedSession_whenCreate_thenReturnExistingSession() {
        var firstSession = new ExerciseSessionDto();
        firstSession.setId("existing-session-id");
        firstSession.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        firstSession.setFlashcardDeckId("flashcardDeckVertex2");
        var savedSession = controller.create(firstSession);

        var secondSession = new ExerciseSessionDto();
        secondSession.setId("new-session-id");
        secondSession.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        secondSession.setFlashcardDeckId("flashcardDeckVertex2");

        var returnedSession = controller.create(secondSession);

        assertEquals(savedSession.getId(), returnedSession.getId());
    }

    @Test
    void givenMatchingCompletedSession_whenCreate_thenCreateNewSession() {
        var firstSession = new ExerciseSessionDto();
        firstSession.setId("completed-session-id");
        firstSession.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        firstSession.setFlashcardDeckId("flashcardDeckVertex2");
        var savedSession = controller.create(firstSession);

        var existingSessionVertex = ExerciseSessionVertex.findById(traversalSource, savedSession.getId());
        existingSessionVertex.setCompleted(true);

        var secondSession = new ExerciseSessionDto();
        secondSession.setId("fresh-session-id");
        secondSession.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        secondSession.setFlashcardDeckId("flashcardDeckVertex2");

        var returnedSession = controller.create(secondSession);

        assertNotEquals(savedSession.getId(), returnedSession.getId());
        assertEquals(secondSession.getId(), returnedSession.getId());
    }

    @Test
    void givenNewlyCreatedSession_whenNext_thenReturnExercise() {
        var session = new ExerciseSessionDto();
        session.setId("new-id");
        session.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        session.setFlashcardDeckId("flashcardDeckVertex2");

        controller.create(session);
        var newSession = controller.nextExercise(session.getId());

        assertNotNull(newSession);
        assertNotNull(newSession.getExercise());
    }

    @Test
    void givenNothing_whenCreateLearnFlashcardSession_thenReturnValidExerciseSession() {
        var session = new ExerciseSessionDto();
        session.setId("learn-session-id");
        session.setType(ExerciseSessionTypes.LEARN_FLASHCARD);
        session.setFlashcardDeckId("flashcardDeckVertex2");

        var savedSession = controller.create(session);

        assertNotNull(savedSession);
        assertNotNull(savedSession.getExercise());
        assertEquals(session.getId(), savedSession.getId());
        assertEquals(ExerciseSessionTypes.LEARN_FLASHCARD, savedSession.getType());
    }

    @Test
    void givenNewlyCreatedSession_whenGetConfig_thenReturnDefaultShuffleFlashcardConfig() {
        createSession("shuffle-test-id");

        var session = controller.get("shuffle-test-id");
        var sessionConfig = (ExerciseSessionOptionsLearnFlashcardsDto)controller.getConfig("shuffle-test-id");

        assertNotNull(sessionConfig);
        assertFalse(sessionConfig.getShuffleFlashcardsEnabled());
    }

    @Test
    void givenShuffleFlashcardsEnabled_whenUpdateConfig_thenUpdateSessionConfig() {
        createSession("shuffle-on-test-id");
        var sessionConfig = (ExerciseSessionOptionsLearnFlashcardsDto) controller.getConfig("shuffle-on-test-id");
        sessionConfig.setShuffleFlashcardsEnabled(true);

        var updatedSessionConfig = updateConfig("shuffle-on-test-id", sessionConfig);

        assertNotNull(updatedSessionConfig);
        assertTrue(updatedSessionConfig.getShuffleFlashcardsEnabled());
    }

    @Test
    void givenShuffleFlashcardsDisabled_whenUpdateConfig_thenUpdateSessionConfig() {
        createSession("shuffle-off-test-id");
        var sessionConfig = (ExerciseSessionOptionsLearnFlashcardsDto) controller.getConfig("shuffle-off-test-id");
        sessionConfig.setShuffleFlashcardsEnabled(false);

        var updatedSessionConfig = updateConfig("shuffle-off-test-id", sessionConfig);

        assertNotNull(updatedSessionConfig);
        assertFalse(updatedSessionConfig.getShuffleFlashcardsEnabled());
    }

    @Test
    void givenLearnSession_whenUpdateConfig_thenReturnsNewCurrentExercise() {
        var initialSession = createSession("update-config-new-exercise-test-id");
        assertNotNull(initialSession.getExercise());
        var initialExerciseId = initialSession.getExercise().getId();

        var sessionConfig = (ExerciseSessionOptionsLearnFlashcardsDto) controller.getConfig("update-config-new-exercise-test-id");
        sessionConfig.setShuffleFlashcardsEnabled(!sessionConfig.getShuffleFlashcardsEnabled());

        var updatedSession = controller.updateConfig("update-config-new-exercise-test-id", sessionConfig);

        assertNotNull(updatedSession);
        assertNotNull(updatedSession.getExercise());
        assertNotEquals(initialExerciseId, updatedSession.getExercise().getId());
    }

    ExerciseSessionDto createSession(String id) {
        var session = new ExerciseSessionDto();
        session.setId(id);
        session.setType(ExerciseSessionTypes.LEARN_FLASHCARD);
        session.setFlashcardDeckId("flashcardDeckVertex2");

        return controller.create(session);
    }

    ExerciseSessionOptionsLearnFlashcardsDto updateConfig(String id, ExerciseSessionOptionsLearnFlashcardsDto config) {
        controller.updateConfig(id, config);

        var sessionOptions = controller.getConfig(id);
        return (ExerciseSessionOptionsLearnFlashcardsDto)sessionOptions;
    }


//
//    @Test
//    void givenNewlyCreatedSession_whenGet_thenReturnExerciseSession() {
//         var session = new ExerciseSessionDto();
//         session.setId("new-id");
//         session.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
//         session.setFlashcardDeckId(FlashcardDeck.Id);
//
//         controller.create(session);
//         var savedSession = controller.get(session.getId());
//
//         assertNotNull(savedSession);
//         assertNotNull(savedSession.getExercise());
//    }
//
//    @Test
//    void givenNewlyCreatedSession_whenGet_thenReturnDefaultConfig() {
//        createSession("new-id");
//
//        var session = controller.get("new-id");
//        var sessionConfig = session.getOptions();
//
//        assertNotNull(sessionConfig);
//        assertEquals(false, sessionConfig.getTextToSpeechEnabled());
//        assertEquals(false, sessionConfig.getRetypeCorrectAnswerEnabled());
//        assertEquals(null, sessionConfig.getInitialFlashcardLanguageId());
//
//        assertNotNull(sessionConfig.getAnswerLanguageIds());
//        assertEquals(0, sessionConfig.getAnswerLanguageIds().length);
//    }
//
//    @Test
//    void givenTextToSpeechEnabled_whenUpdateConfig_thenUpdateSessionConfig() {
//        var sessionConfig = new ExerciseSessionOptionsDto();
//        sessionConfig.setTextToSpeechEnabled(true);
//
//        createSession("new-id");
//        var updatedSessionConfig = updateConfig("new-id", sessionConfig);
//
//        assertNotNull(updatedSessionConfig);
//        assertEquals(true, updatedSessionConfig.getTextToSpeechEnabled());
//    }
//
//    @Test
//    void givenRetypeCorrectAnswerEnabled_whenUpdateConfig_thenUpdateSessionConfig() {
//        var sessionConfig = new ExerciseSessionOptionsDto();
//        sessionConfig.setRetypeCorrectAnswerEnabled(true);
//
//        createSession("new-id");
//        var updatedSessionConfig = updateConfig("new-id", sessionConfig);
//
//        assertNotNull(updatedSessionConfig);
//        assertEquals(true, updatedSessionConfig.getRetypeCorrectAnswerEnabled());
//    }
//
//    @Test
//    void givenInitialFlashcardLanguageId_whenUpdateConfig_thenUpdateSessionConfig() {
//        var sessionConfig = new ExerciseSessionOptionsDto();
//        sessionConfig.setInitialFlashcardLanguageId("FlashcardLanguage");
//
//        createSession("new-id");
//        var updatedSessionConfig = updateConfig("new-id", sessionConfig);
//
//        assertNotNull(updatedSessionConfig);
//        assertEquals("FlashcardLanguage", updatedSessionConfig.getInitialFlashcardLanguageId());
//    }
//
//    @Test
//    void givenAnswerLanguageIds_whenUpdateConfig_thenUpdateSessionConfig() {
//        var sessionConfig = new ExerciseSessionOptionsDto();
//        sessionConfig.setAnswerLanguageIds(new String[] { "langVertex1", "langVertex2" });
//
//        createSession("new-id");
//        var updatedSessionConfig = updateConfig("new-id", sessionConfig);
//
//        assertNotNull(updatedSessionConfig);
//        assertNotNull(updatedSessionConfig.getAnswerLanguageIds());
//        assertEquals(2, updatedSessionConfig.getAnswerLanguageIds().length);
//        assertEquals("langVertex1", updatedSessionConfig.getAnswerLanguageIds()[0]);
//        assertEquals("langVertex2", updatedSessionConfig.getAnswerLanguageIds()[1]);
//    }
//
//    ExerciseSessionDto createSession(String id) {
//        var session = new ExerciseSessionDto();
//        session.setId(id);
//        session.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
//        session.setFlashcardDeckId(FlashcardDeck.Id);
//
//        return controller.create(session);
//    }
//
//    ExerciseSessionOptionsDto updateConfig(String id, ExerciseSessionOptionsDto config) {
//        var session = controller.updateConfig(id, config);
//        return session.getOptions();
//    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }
}

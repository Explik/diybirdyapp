package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionOptionsDto;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static com.explik.diybirdyapp.TestDataConstants.*;

@SpringBootTest
public class ExerciseSessionControllerIntegrationTests {
    @Autowired
    ExerciseSessionController controller;

    @Test
    void givenNothing_whenCreate_thenReturnExerciseSession() {
        var session = new ExerciseSessionDto();
        session.setId("new-id");
        session.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        session.setFlashcardDeckId(FlashcardDeck.Id);

        var savedSession = controller.create(session);

        assertNotNull(savedSession);
        assertNotNull(savedSession.getExercise());
        assertEquals(session.getId(), savedSession.getId());
        assertEquals(session.getType(), savedSession.getType());
    }

    @Test
    void givenNewlyCreatedSession_whenNext_thenReturnExercise() {
        var session = new ExerciseSessionDto();
        session.setId("new-id");
        session.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        session.setFlashcardDeckId(FlashcardDeck.Id);

        controller.create(session);
        var newSession = controller.nextExercise(session.getId());

        assertNotNull(newSession);
        assertNotNull(newSession.getExercise());
    }

    @Test
    void givenNewlyCreatedSession_whenGet_thenReturnExerciseSession() {
         var session = new ExerciseSessionDto();
         session.setId("new-id");
         session.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
         session.setFlashcardDeckId(FlashcardDeck.Id);

         controller.create(session);
         var savedSession = controller.get(session.getId());

         assertNotNull(savedSession);
         assertNotNull(savedSession.getExercise());
    }

    @Test
    void givenNewlyCreatedSession_whenGet_thenReturnDefaultConfig() {
        createSession("new-id");

        var session = controller.get("new-id");
        var sessionConfig = session.getOptions();

        assertNotNull(sessionConfig);
        assertEquals(false, sessionConfig.getTextToSpeechEnabled());
        assertEquals(false, sessionConfig.getRetypeCorrectAnswerEnabled());
        assertEquals(null, sessionConfig.getInitialFlashcardLanguageId());

        assertNotNull(sessionConfig.getAnswerLanguageIds());
        assertEquals(0, sessionConfig.getAnswerLanguageIds().length);
    }

    @Test
    void givenTextToSpeechEnabled_whenUpdateConfig_thenUpdateSessionConfig() {
        var sessionConfig = new ExerciseSessionOptionsDto();
        sessionConfig.setTextToSpeechEnabled(true);

        createSession("new-id");
        var updatedSessionConfig = updateConfig("new-id", sessionConfig);

        assertNotNull(updatedSessionConfig);
        assertEquals(true, updatedSessionConfig.getTextToSpeechEnabled());
    }

    @Test
    void givenRetypeCorrectAnswerEnabled_whenUpdateConfig_thenUpdateSessionConfig() {
        var sessionConfig = new ExerciseSessionOptionsDto();
        sessionConfig.setRetypeCorrectAnswerEnabled(true);

        createSession("new-id");
        var updatedSessionConfig = updateConfig("new-id", sessionConfig);

        assertNotNull(updatedSessionConfig);
        assertEquals(true, updatedSessionConfig.getRetypeCorrectAnswerEnabled());
    }

    @Test
    void givenInitialFlashcardLanguageId_whenUpdateConfig_thenUpdateSessionConfig() {
        var sessionConfig = new ExerciseSessionOptionsDto();
        sessionConfig.setInitialFlashcardLanguageId("FlashcardLanguage");

        createSession("new-id");
        var updatedSessionConfig = updateConfig("new-id", sessionConfig);

        assertNotNull(updatedSessionConfig);
        assertEquals("FlashcardLanguage", updatedSessionConfig.getInitialFlashcardLanguageId());
    }

    @Test
    void givenAnswerLanguageIds_whenUpdateConfig_thenUpdateSessionConfig() {
        var sessionConfig = new ExerciseSessionOptionsDto();
        sessionConfig.setAnswerLanguageIds(new String[] { "langVertex1", "langVertex2" });

        createSession("new-id");
        var updatedSessionConfig = updateConfig("new-id", sessionConfig);

        assertNotNull(updatedSessionConfig);
        assertNotNull(updatedSessionConfig.getAnswerLanguageIds());
        assertEquals(2, updatedSessionConfig.getAnswerLanguageIds().length);
        assertEquals("langVertex1", updatedSessionConfig.getAnswerLanguageIds()[0]);
        assertEquals("langVertex2", updatedSessionConfig.getAnswerLanguageIds()[1]);
    }

    ExerciseSessionDto createSession(String id) {
        var session = new ExerciseSessionDto();
        session.setId(id);
        session.setType(ExerciseSessionTypes.REVIEW_FLASHCARD);
        session.setFlashcardDeckId(FlashcardDeck.Id);

        return controller.create(session);
    }

    ExerciseSessionOptionsDto updateConfig(String id, ExerciseSessionOptionsDto config) {
        var session = controller.updateConfig(id, config);
        return session.getOptions();
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }
}

package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionDto;
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

    @TestConfiguration
    public static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }
}

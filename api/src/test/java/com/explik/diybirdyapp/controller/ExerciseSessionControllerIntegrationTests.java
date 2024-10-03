package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.ExerciseSessionDto;
import com.explik.diybirdyapp.graph.vertex.factory.FlashcardDeckVertexFactory;
import com.explik.diybirdyapp.graph.vertex.factory.FlashcardVertexFactory;
import com.explik.diybirdyapp.graph.vertex.factory.LanguageVertexFactory;
import com.explik.diybirdyapp.graph.vertex.factory.TextContentVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ExerciseSessionControllerIntegrationTests {
    private static final String FLASHCARD_DECK_ID = "flashcard-deck-id";

    @Autowired
    ExerciseSessionController controller;

    @Test
    void givenNothing_whenCreate_thenReturnExerciseSession() {
        var session = new ExerciseSessionDto();
        session.setId("new-id");
        session.setType("flashcard-review");
        session.setFlashcardDeckId(FLASHCARD_DECK_ID);

        var savedSession = controller.create(session);

        assertNotNull(savedSession);
        assertEquals(session.getId(), savedSession.getId());
        assertEquals(session.getType(), savedSession.getType());
    }

    @Test
    void givenNewlyCreatedSession_whenNext_thenReturnExercise() {
        var session = new ExerciseSessionDto();
        session.setId("new-id");
        session.setType("flashcard-review");
        session.setFlashcardDeckId(FLASHCARD_DECK_ID);

        controller.create(session);
        var nextExercise = controller.nextExercise(session.getId());

        assertNotNull(nextExercise);
    }

    @TestConfiguration
    public static class InMemoryGraphTestConfiguration {
        public static final String TRANSLATION_FLASHCARD_DECK_ID = "translation-flashcard-deck-id";

        public static final String EMPTY_FLASHCARD_DECK_ID = "empty-flashcard-deck-id";
        public static final String ONE_CARD_FLASHCARD_DECK_ID = "one-card-flashcard-deck-id";
        public static final String TWO_CARD_FLASHCARD_DECK_ID = "two-card-flashcard-deck-id";
        public static final String MULTI_CARD_FLASHCARD_DECK_ID = "multi-card-flashcard-deck-id";

        @Autowired
        FlashcardVertexFactory flashcardVertexFactory;

        @Autowired
        FlashcardDeckVertexFactory flashcardDeckVertexFactory;

        @Autowired
        LanguageVertexFactory languageVertexFactory;

        @Autowired
        TextContentVertexFactory textContentVertexFactory;

        @Bean
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            var traversal = graph.traversal();

            var lang1 = languageVertexFactory.create(traversal, new LanguageVertexFactory.Options("lang1", "", ""));
            var lang2 = languageVertexFactory.create(traversal, new LanguageVertexFactory.Options("lang2", "", ""));
            var lang3 = languageVertexFactory.create(traversal, new LanguageVertexFactory.Options("lang3", "", ""));

            var content1 = textContentVertexFactory.create(traversal, new TextContentVertexFactory.Options("content1", "", lang1));
            var content2 = textContentVertexFactory.create(traversal, new TextContentVertexFactory.Options("content2", "", lang2));
            var content3 = textContentVertexFactory.create(traversal, new TextContentVertexFactory.Options("content3", "", lang3));

            var flashard0 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("pre-existent-id", content1, content2));
            var flashcard1 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard1", content1, content2));
            var flashcard2 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard2", content2, content3));

            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options(FLASHCARD_DECK_ID, "", List.of(flashard0, flashcard1, flashcard2)));

            return traversal;
        }
    }
}

package com.explik.diybirdyapp.persistence.vertex.manager;

import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperationsFlashcardReview;
import com.explik.diybirdyapp.persistence.vertexFactory.FlashcardDeckVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.FlashcardVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.LanguageVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.TextContentVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExerciseSessionFlashcardReviewVertexFactoryUnitTests {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    ExerciseSessionOperationsFlashcardReview factory;

    @Test
    public void givenEmptyFlashcardDeck_whenInit_thenThrowsException() {
        var model = new ExerciseSessionModel();
        model.setId("new-id");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.EMPTY_FLASHCARD_DECK_ID);

        assertThrows(IllegalArgumentException.class, () -> {
            factory.init(traversalSource, model);
        });
    }

    @Test
    public void givenNonEmptyFlashcardDeck_whenInit_thenReturnsVertex() {
        var model = new ExerciseSessionModel();
        model.setId("new-id-1");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.ONE_CARD_FLASHCARD_DECK_ID);

        var vertex = factory.init(traversalSource, model);

        assertNotNull(vertex);
        assertNotNull(vertex.getExercise());
    }

    @Test
    public void givenPartiallyReviewedFlashcardDeck_whenNext_thenReturnsVertex() {
        var model = new ExerciseSessionModel();
        model.setId("new-id-3");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.TWO_CARD_FLASHCARD_DECK_ID);

        factory.init(traversalSource, model); // Initializes session and reviews 1/2 cards
        var nextExercise = factory.next(traversalSource, model.getId()); // Reviews 2/2 cards

        assertNotNull(nextExercise);
    }

    @Test
    public void givenFullyReviewedFlashcardDeck_whenNext_thenReturnsNull() {
        var model = new ExerciseSessionModel();
        model.setId("new-id-4");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.TWO_CARD_FLASHCARD_DECK_ID);

        factory.init(traversalSource, model); // Initializes session and reviews 1/2 cards
        factory.next(traversalSource, model.getId()); // Reviews 2/2 cards
        var nextExercise = factory.next(traversalSource, model.getId()); // No more cards to review

        assertNull(nextExercise);
    }

    @Test
    public void givenNonEmptyDeck_whenNext_thenReturnsUniqueVertexEachTime() {
        var model = new ExerciseSessionModel();
        model.setId("new-id-5");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.MULTI_CARD_FLASHCARD_DECK_ID);

        var nextExercise0 = factory.init(traversalSource, model).getExercise(); // Initializes session and reviews 1/x cards
        var nextExercise1 = factory.next(traversalSource, model.getId()); // Reviews 2/x cards
        var nextExercise2 = factory.next(traversalSource, model.getId()); // Reviews 3/x cards
        var nextExercise3 = factory.next(traversalSource, model.getId()); // Reviews 4/x cards

        assertNotEquals(nextExercise0.getContent().getId(), nextExercise1.getContent().getId());
        assertNotEquals(nextExercise1.getContent().getId(), nextExercise2.getContent().getId());
        assertNotEquals(nextExercise2.getContent().getId(), nextExercise3.getContent().getId());
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
            var content4 = textContentVertexFactory.create(traversal, new TextContentVertexFactory.Options("content4", "", lang1));

            var flashard0 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("pre-existent-id", content1, content2));
            var flashcard1 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard1", content1, content2));
            var flashcard2 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard2", content2, content3));
            var flashcard3 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard3", content3, content4));

            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options(EMPTY_FLASHCARD_DECK_ID, "", List.of()));
            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options(ONE_CARD_FLASHCARD_DECK_ID, "", List.of(flashcard1)));
            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options(TWO_CARD_FLASHCARD_DECK_ID, "", List.of(flashcard1, flashcard2)));
            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options(MULTI_CARD_FLASHCARD_DECK_ID, "", List.of(flashard0, flashcard1, flashcard2, flashcard3)));

            return traversal;
        }
    }
}

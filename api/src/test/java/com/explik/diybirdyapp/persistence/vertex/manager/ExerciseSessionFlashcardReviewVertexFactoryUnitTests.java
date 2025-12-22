package com.explik.diybirdyapp.persistence.vertex.manager;

import com.explik.diybirdyapp.persistence.command.CreateFlashcardDeckVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateFlashcardVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.operation.ExerciseCreationContext;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperationsReviewFlashcardDeck;
import com.explik.diybirdyapp.persistence.command.CreateLanguageVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateTextContentVertexCommand;
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
    ExerciseSessionOperationsReviewFlashcardDeck factory;

    @Test
    public void givenEmptyFlashcardDeck_whenInit_thenThrowsException() {
        var model = new ExerciseSessionModel();
        model.setId("new-id");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.EMPTY_FLASHCARD_DECK_ID);

        var context = ExerciseCreationContext.createDefault(model);

        assertThrows(IllegalArgumentException.class, () -> {
            factory.init(traversalSource, context);
        });
    }

    @Test
    public void givenNonEmptyFlashcardDeck_whenInit_thenReturnsVertex() {
        var model = new ExerciseSessionModel();
        model.setId("new-id-1");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.ONE_CARD_FLASHCARD_DECK_ID);

        var context = ExerciseCreationContext.createDefault(model);

        var vertex = factory.init(traversalSource, context);

        assertNotNull(vertex);
        assertNotNull(vertex.getExercise());
    }

    @Test
    public void givenPartiallyReviewedFlashcardDeck_whenNext_thenReturnsVertex() {
        var model = new ExerciseSessionModel();
        model.setId("new-id-3");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.TWO_CARD_FLASHCARD_DECK_ID);

        var context = ExerciseCreationContext.createDefault(model);

        factory.init(traversalSource, context); // Initializes session and reviews 1/2 cards
        var nextExercise = factory.nextExercise(traversalSource, context); // Reviews 2/2 cards

        assertNotNull(nextExercise);
    }

    @Test
    public void givenFullyReviewedFlashcardDeck_whenNext_thenReturnsNull() {
        var model = new ExerciseSessionModel();
        model.setId("new-id-4");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.TWO_CARD_FLASHCARD_DECK_ID);

        var context = ExerciseCreationContext.createDefault(model);

        factory.init(traversalSource, context); // Initializes session and reviews 1/2 cards
        factory.nextExercise(traversalSource, context); // Reviews 2/2 cards
        var newSessionState = factory.nextExercise(traversalSource, context); // No more cards to review

        assertTrue(newSessionState.getCompleted());
    }

    @Test
    public void givenNonEmptyDeck_whenNext_thenReturnsUniqueVertexEachTime() {
        var model = new ExerciseSessionModel();
        model.setId("new-id-5");
        model.setType("flashcard-review");
        model.setFlashcardDeckId(InMemoryGraphTestConfiguration.MULTI_CARD_FLASHCARD_DECK_ID);

        var context = ExerciseCreationContext.createDefault(model);

        var nextExercise0 = factory.init(traversalSource, context).getExercise(); // Initializes session and reviews 1/x cards
        var nextExercise1 = factory.nextExercise(traversalSource, context).getExercise(); // Reviews 2/x cards
        var nextExercise2 = factory.nextExercise(traversalSource, context).getExercise(); // Reviews 3/x cards
        var nextExercise3 = factory.nextExercise(traversalSource, context).getExercise(); // Reviews 4/x cards

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
        CommandHandler<CreateFlashcardVertexCommand> createFlashcardVertexCommandHandler;

        @Autowired
        CommandHandler<CreateFlashcardDeckVertexCommand> createFlashcardDeckVertexCommandHandler;

        @Autowired
        CommandHandler<CreateLanguageVertexCommand> createLanguageVertexCommandHandler;

        @Autowired
        CommandHandler<CreateTextContentVertexCommand> createTextContentVertexCommandHandler;

        @Bean
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            var traversal = graph.traversal();

            var createLang1Command = new CreateLanguageVertexCommand();
            createLang1Command.setId("lang1");
            createLang1Command.setName("");
            createLang1Command.setIsoCode("");
            createLanguageVertexCommandHandler.handle(createLang1Command);
            var lang1 = LanguageVertex.getById(traversal, "lang1");

            var createLang2Command = new CreateLanguageVertexCommand();
            createLang2Command.setId("lang2");
            createLang2Command.setName("");
            createLang2Command.setIsoCode("");
            createLanguageVertexCommandHandler.handle(createLang2Command);
            var lang2 = LanguageVertex.getById(traversal, "lang2");

            var createLang3Command = new CreateLanguageVertexCommand();
            createLang3Command.setId("lang3");
            createLang3Command.setName("");
            createLang3Command.setIsoCode("");
            createLanguageVertexCommandHandler.handle(createLang3Command);
            var lang3 = LanguageVertex.getById(traversal, "lang3");

            var createContent1Command = new CreateTextContentVertexCommand();
            createContent1Command.setId("content1");
            createContent1Command.setValue("");
            createContent1Command.setLanguage(lang1);
            createTextContentVertexCommandHandler.handle(createContent1Command);
            var content1 = TextContentVertex.getById(traversal, "content1");

            var createContent2Command = new CreateTextContentVertexCommand();
            createContent2Command.setId("content2");
            createContent2Command.setValue("");
            createContent2Command.setLanguage(lang2);
            createTextContentVertexCommandHandler.handle(createContent2Command);
            var content2 = TextContentVertex.getById(traversal, "content2");

            var createContent3Command = new CreateTextContentVertexCommand();
            createContent3Command.setId("content3");
            createContent3Command.setValue("");
            createContent3Command.setLanguage(lang3);
            createTextContentVertexCommandHandler.handle(createContent3Command);
            var content3 = TextContentVertex.getById(traversal, "content3");

            var createContent4Command = new CreateTextContentVertexCommand();
            createContent4Command.setId("content4");
            createContent4Command.setValue("");
            createContent4Command.setLanguage(lang1);
            createTextContentVertexCommandHandler.handle(createContent4Command);
            var content4 = TextContentVertex.getById(traversal, "content4");

            var createFlashcard0Command = new CreateFlashcardVertexCommand();
            createFlashcard0Command.setId("pre-existent-id");
            createFlashcard0Command.setLeftContent(content1);
            createFlashcard0Command.setRightContent(content2);
            createFlashcardVertexCommandHandler.handle(createFlashcard0Command);
            var flashard0 = FlashcardVertex.getById(traversal, "pre-existent-id");
            
            var createFlashcard1Command = new CreateFlashcardVertexCommand();
            createFlashcard1Command.setId("flashcard1");
            createFlashcard1Command.setLeftContent(content1);
            createFlashcard1Command.setRightContent(content2);
            createFlashcardVertexCommandHandler.handle(createFlashcard1Command);
            var flashcard1 = FlashcardVertex.getById(traversal, "flashcard1");
            
            var createFlashcard2Command = new CreateFlashcardVertexCommand();
            createFlashcard2Command.setId("flashcard2");
            createFlashcard2Command.setLeftContent(content2);
            createFlashcard2Command.setRightContent(content3);
            createFlashcardVertexCommandHandler.handle(createFlashcard2Command);
            var flashcard2 = FlashcardVertex.getById(traversal, "flashcard2");
            
            var createFlashcard3Command = new CreateFlashcardVertexCommand();
            createFlashcard3Command.setId("flashcard3");
            createFlashcard3Command.setLeftContent(content3);
            createFlashcard3Command.setRightContent(content4);
            createFlashcardVertexCommandHandler.handle(createFlashcard3Command);
            var flashcard3 = FlashcardVertex.getById(traversal, "flashcard3");

            var createEmptyDeckCommand = new CreateFlashcardDeckVertexCommand();
            createEmptyDeckCommand.setId(EMPTY_FLASHCARD_DECK_ID);
            createEmptyDeckCommand.setName("");
            createEmptyDeckCommand.setFlashcards(List.of());
            createFlashcardDeckVertexCommandHandler.handle(createEmptyDeckCommand);

            var createOneCardDeckCommand = new CreateFlashcardDeckVertexCommand();
            createOneCardDeckCommand.setId(ONE_CARD_FLASHCARD_DECK_ID);
            createOneCardDeckCommand.setName("");
            createOneCardDeckCommand.setFlashcards(List.of(flashcard1));
            createFlashcardDeckVertexCommandHandler.handle(createOneCardDeckCommand);

            var createTwoCardDeckCommand = new CreateFlashcardDeckVertexCommand();
            createTwoCardDeckCommand.setId(TWO_CARD_FLASHCARD_DECK_ID);
            createTwoCardDeckCommand.setName("");
            createTwoCardDeckCommand.setFlashcards(List.of(flashcard1, flashcard2));
            createFlashcardDeckVertexCommandHandler.handle(createTwoCardDeckCommand);

            var createMultiCardDeckCommand = new CreateFlashcardDeckVertexCommand();
            createMultiCardDeckCommand.setId(MULTI_CARD_FLASHCARD_DECK_ID);
            createMultiCardDeckCommand.setName("");
            createMultiCardDeckCommand.setFlashcards(List.of(flashard0, flashcard1, flashcard2, flashcard3));
            createFlashcardDeckVertexCommandHandler.handle(createMultiCardDeckCommand);

            return traversal;
        }
    }
}

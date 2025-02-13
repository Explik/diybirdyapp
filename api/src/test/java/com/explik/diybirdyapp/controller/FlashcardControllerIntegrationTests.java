package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.TestEventListener;
import com.explik.diybirdyapp.controller.dto.content.FlashcardContentTextDto;
import com.explik.diybirdyapp.event.FlashcardAddedEvent;
import com.explik.diybirdyapp.event.FlashcardUpdatedEvent;
import com.explik.diybirdyapp.persistence.command.CommandHandler;
import com.explik.diybirdyapp.persistence.command.LockFlashcardContentCommand;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.explik.diybirdyapp.controller.dto.content.FlashcardDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static com.explik.diybirdyapp.TestDataConstants.*;

@SpringBootTest
public class FlashcardControllerIntegrationTests {
    @Autowired
    CommandHandler<LockFlashcardContentCommand> lockContentCommandHandler;

    @Autowired
    FlashcardController controller;

    // TODO Initialize test data

    @Autowired
    TestEventListener<FlashcardAddedEvent> flashcardAddedEventListener;

    @Autowired
    TestEventListener<FlashcardUpdatedEvent> flashcardUpdatedEventTestEventListener;

    @BeforeEach
    void setUp() {
        flashcardAddedEventListener.reset();
        flashcardUpdatedEventTestEventListener.reset();
    }

    @Test
    void givenNothing_whenCreate_thenReturnFlashcard() {
        var flashcard = createFlashcard();

        var created = controller.create(flashcard);

        assertNotNull(created);
    }

    @Test
    void givenNothing_whenCreate_thenPublishFlashcardAddedEvent() {
        var flashcard = createFlashcard();

        var created = controller.create(flashcard);

        assertThat(flashcardAddedEventListener.getEvents())
                .last()
                .hasFieldOrPropertyWithValue("flashcardId", created.getId());
    }

    @Test
    void givenNewlyCreatedFlashcard_whenUpdateContent_thenReturnFlashcard() {
        var flashcard = createFlashcard();
        var createdFlashcard = controller.create(flashcard);
        setLeftTextValue(createdFlashcard, "new-left-value");
        setRightTextValue(createdFlashcard, "new-right-value");

        var updated = controller.update(createdFlashcard);

        assertNotNull(updated);
        assertEquals("new-left-value", getLeftTextValue(updated));
        assertEquals("new-right-value", getRightTextValue(updated));
    }

    @Test
    void givenStaticFlashcard_whenUpdateContent_thenReturnFlashcard() {
        var flashcard = createFlashcard();
        var created = controller.create(flashcard);
        setLeftTextValue(created, "new-left-value");

        lockContentCommandHandler.handle(
                new LockFlashcardContentCommand(created.getId()));

        var updated = controller.update(created);

        assertNotEquals(created.getId(), updated.getId());
    }

    @Test
    void givenNewlyCreatedFlashcard_whenUpdateContent_thenPublishFlashcardUpdatedEvent() {
        var flashcard = createFlashcard();
        var created = controller.create(flashcard);
        setLeftTextValue(created, "new-left-value");

        controller.update(created);

        assertThat(flashcardUpdatedEventTestEventListener.getEvents())
                .last()
                .hasFieldOrPropertyWithValue("flashcardId", created.getId());
    }

    protected FlashcardDto createFlashcard() {
        // IMPORTANT: Relies on data from DataInitializer.addInitialFlashcardData()
        var frontContent = new FlashcardContentTextDto();
        frontContent.setText("left-value");
        frontContent.setLanguageId(Languages.Danish.Id);

        var backContent = new FlashcardContentTextDto();
        backContent.setText("right-value");
        backContent.setLanguageId(Languages.English.Id);

        var flashcard = new FlashcardDto();
        flashcard.setId("id");
        flashcard.setDeckId(FlashcardDeck.Id);
        flashcard.setFrontContent(frontContent);
        flashcard.setBackContent(backContent);

        return flashcard;
    }

    private String getLeftTextValue(FlashcardDto flashcard) {
        return ((FlashcardContentTextDto)flashcard.getFrontContent()).getText();
    }

    private String getRightTextValue(FlashcardDto flashcard) {
        return ((FlashcardContentTextDto)flashcard.getBackContent()).getText();
    }

    private void setLeftTextValue(FlashcardDto flashcard, String value) {
        ((FlashcardContentTextDto)flashcard.getFrontContent()).setText(value);
        ((FlashcardContentTextDto)flashcard.getFrontContent()).setLanguageId(null);
    }

    private void setRightTextValue(FlashcardDto flashcard, String value) {
        ((FlashcardContentTextDto)flashcard.getBackContent()).setText(value);
        ((FlashcardContentTextDto)flashcard.getBackContent()).setLanguageId(null);
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            return graph.traversal();
        }
    }
}

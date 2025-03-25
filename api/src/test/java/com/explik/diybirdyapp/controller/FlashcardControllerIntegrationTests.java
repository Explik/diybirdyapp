package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.ContentTypes;
import com.explik.diybirdyapp.IntegrationTestConfigurations;
import com.explik.diybirdyapp.TestDataService;
import com.explik.diybirdyapp.TestEventListener;
import com.explik.diybirdyapp.controller.dto.content.FlashcardContentDto;
import com.explik.diybirdyapp.controller.dto.content.FlashcardContentTextDto;
import com.explik.diybirdyapp.event.FlashcardAddedEvent;
import com.explik.diybirdyapp.event.FlashcardUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.explik.diybirdyapp.controller.dto.content.FlashcardDto;
import org.springframework.boot.test.context.TestConfiguration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static com.explik.diybirdyapp.TestDataService.*;

@SpringBootTest
public class FlashcardControllerIntegrationTests {
    @Autowired
    FlashcardController controller;

    @Autowired
    TestEventListener<FlashcardAddedEvent> flashcardAddedEventListener;

    @Autowired
    TestEventListener<FlashcardUpdatedEvent> flashcardUpdatedEventTestEventListener;

    @Autowired
    TestDataService testDataService;

    @BeforeEach
    void setUp() {
        testDataService.clear();
        testDataService.populateFlashcardLanguages();
        testDataService.populateFlashcardDecks();
    }

    // POST /api/flashcard tests
    @Test
    void givenNothing_whenCreate_thenReturnFlashcard() {
        var flashcard = createTextFlashcard();

        var created = controller.create(flashcard);

        assertNotNull(created);
    }

    @Test
    void givenNothing_whenCreate_thenPublishFlashcardAddedEvent() {
        var flashcard = createTextFlashcard();

        var created = controller.create(flashcard);

        assertThat(flashcardAddedEventListener.getEvents())
                .last()
                .hasFieldOrPropertyWithValue("flashcardId", created.getId());
    }

    // POST /api/flashcard/rich tests
    @Test
    void givenNothing_whenCreateRich_thenReturnFlashcard() {
        var flashcard = createTextFlashcard();
        var created = controller.createRich(flashcard, null);

        assertNotNull(created);
    }

    @Test
    void givenNothing_whenCreateRich_thenPublishFlashcardAddedEvent() {
        var flashcard = createTextFlashcard();
        var created = controller.createRich(flashcard, null);

        assertThat(flashcardAddedEventListener.getEvents())
                .last()
                .hasFieldOrPropertyWithValue("flashcardId", created.getId());
    }

    // GET /api/flashcard/{id} tests
    @Test
    void givenNewlyCreatedFlashcard_whenGetById_thenReturnFlashcard() {
        var flashcard = createTextFlashcard();
        var created = controller.create(flashcard);

        var found = controller.get(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void givenNonExistentFlashcard_whenGet_thenThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> controller.get("Non-existent id"));
    }

    // GET /api/flashcard tests
    void givenNothing_whenGetAll_thenReturnEmptyList() {
        var all = controller.getAll(null);

        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    void givenOneNewlyCreatedFlashcard_whenGetAll_thenReturnListWithOneElement() {
        controller.create(createTextFlashcard());

        var all = controller.getAll(null);

        assertNotNull(all);
        assertEquals(1, all.size());
    }

    void givenTwoNewlyCreatedFlashcards_whenGetAll_thenReturnListWithTwoElements() {
        controller.create(createTextFlashcard());
        controller.create(createTextFlashcard());

        var all = controller.getAll(null);

        assertNotNull(all);
        assertEquals(2, all.size());
    }

    // PUT /api/flashcard tests
    @Test
    void givenNewlyCreatedFlashcard_whenUpdateContent_thenPublishFlashcardUpdatedEvent() {
        var flashcard = createTextFlashcard();
        var created = controller.create(flashcard);

        var flashcardChange = createFlashcardChange(created.getId());
        controller.update(flashcardChange);

        assertThat(flashcardUpdatedEventTestEventListener.getEvents())
                .last()
                .hasFieldOrPropertyWithValue("flashcardId", created.getId());
    }

    // PUT /api/flashcard/rich tests
    @Test
    void givenNewlyCreatedFlashcard_whenUpdateContentRich_thenPublishFlashcardUpdatedEvent() {
        var flashcard = createTextFlashcard();
        var created = controller.create(flashcard);

        var flashcardChange = createFlashcardChange(created.getId());
        controller.update(flashcardChange);

        assertThat(flashcardUpdatedEventTestEventListener.getEvents())
                .last()
                .hasFieldOrPropertyWithValue("flashcardId", created.getId());
    }

    // DELETE /api/flashcard/{id} tests
    @Test
    void givenNewlyCreatedFlashcard_whenDelete_thenFlashcardIsDeleted() {
        var flashcard = createTextFlashcard();
        var created = controller.create(flashcard);

        controller.delete(created.getId());

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.get(created.getId()));
    }

    FlashcardDto createTextFlashcard() {
        var leftContent = new FlashcardContentTextDto();
        leftContent.setType(ContentTypes.TEXT);
        leftContent.setLanguageId(LEFT_LANGUAGE_ID);
        leftContent.setText("Left content");

        var rightContent = new FlashcardContentTextDto();
        rightContent.setType(ContentTypes.TEXT);
        rightContent.setLanguageId(RIGHT_LANGUAGE_ID);
        rightContent.setText("Right content");

        return createFlashcard(leftContent, rightContent);
    }

    FlashcardDto createFlashcardChange(String flashcardId) {
        var flashcardContentChange = new FlashcardContentTextDto();
        flashcardContentChange.setType(ContentTypes.TEXT);
        flashcardContentChange.setText("New content");

        var flashcardChange = createTextFlashcard();
        flashcardChange.setId(flashcardId);
        flashcardChange.setFrontContent(flashcardContentChange);

        return flashcardChange;
    }

    FlashcardDto createFlashcard(FlashcardContentDto leftContent, FlashcardContentDto rightContent) {
        var flashcard = new FlashcardDto();
        flashcard.setId("Original id");
        flashcard.setDeckId(FLASHCARD_DECK_1_ID);
        flashcard.setFrontContent(leftContent);
        flashcard.setBackContent(rightContent);

        return flashcard;
    }
    @TestConfiguration
    static class TestConfig extends IntegrationTestConfigurations.Default { }
}

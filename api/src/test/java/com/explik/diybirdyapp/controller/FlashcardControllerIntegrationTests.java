package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.*;
import com.explik.diybirdyapp.event.FlashcardAddedEvent;
import com.explik.diybirdyapp.event.FlashcardUpdatedEvent;
import com.explik.diybirdyapp.model.content.FlashcardContentTextDto;
import com.explik.diybirdyapp.model.content.FlashcardContentUploadAudioDto;
import com.explik.diybirdyapp.model.content.FlashcardContentUploadVideoDto;
import com.explik.diybirdyapp.model.content.FlashcardDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.stream.Stream;

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
    void givenFlashcard_whenCreateRich_thenReturnFlashcard() {
        var flashcard = createTextFlashcard();
        var created = controller.createRich(flashcard, null);

        assertNotNull(created);
    }

    @ParameterizedTest
    @MethodSource("provideFlashcardContentCombinations")
    void givenFlashcardWithUpload_whenCreateRich_thenReturnFlashcard(String leftType, String rightType) {
        var flashcard = createRichFlashcard(leftType, rightType);
        var created = controller.createRich(flashcard.dto(), flashcard.files());

        assertNotNull(created);
    }

    @ParameterizedTest
    @MethodSource("provideFlashcardContentCombinations")
    void givenFlashcardWithUploadButNoFiles_whenCreateRich_thenThrowsException(String leftType, String rightType) {
        var creation = createRichFlashcard(leftType, rightType);

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.createRich(creation.dto(), new MultipartFile[0]));
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

    @ParameterizedTest
    @MethodSource("provideFlashcardContentCombinations")
    void givenNewlyCreatedRichFlashcard_whenGetById_thenReturnFlashcard(String leftType, String rightType) {
        var flashcard = createRichFlashcard(leftType, rightType);
        var created = controller.createRich(flashcard.dto(), flashcard.files());

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

        var flashcardChange = createTextFlashcardChange(created.getId());
        controller.update(flashcardChange);

        assertThat(flashcardUpdatedEventTestEventListener.getEvents())
                .last()
                .hasFieldOrPropertyWithValue("flashcardId", created.getId());
    }

    @Test
    void givenNewlyCreatedFlashcard_whenUpdateContent_thenPublishFlashcardUpdatedEvent2() {
        var flashcard = createTextFlashcard();
        var created = controller.create(flashcard);

        var flashcardChange = createTextFlashcardChange(created.getId());
        flashcardChange.setId(null);
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

        var flashcardChange = createTextFlashcardChange(created.getId());
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

        var flashcard = new FlashcardDto();
        flashcard.setId("Original id");
        flashcard.setDeckId(FLASHCARD_DECK_1_ID);
        flashcard.setFrontContent(leftContent);
        flashcard.setBackContent(rightContent);
        return flashcard;
    }

    FlashcardDto createTextFlashcardChange(String flashcardId) {
        var flashcardContentChange = new FlashcardContentTextDto();
        flashcardContentChange.setType(ContentTypes.TEXT);
        flashcardContentChange.setText("New content");

        var flashcardChange = createTextFlashcard();
        flashcardChange.setId(flashcardId);
        flashcardChange.setFrontContent(flashcardContentChange);

        return flashcardChange;
    }

    TestDataFactories.FlashcardCreation createRichFlashcard(String leftSide, String rightSide) {
        var flashcardFactory = new TestDataFactories.FlashcardCreationFactory();
        var flashcard = flashcardFactory.create(leftSide, rightSide);
        flashcard.dto().setDeckId(FLASHCARD_DECK_1_ID);

        if (flashcard.dto().getFrontContent() instanceof FlashcardContentTextDto textContent)
            textContent.setLanguageId(LEFT_LANGUAGE_ID);
        if (flashcard.dto().getFrontContent() instanceof FlashcardContentUploadAudioDto audioDto)
            audioDto.setLanguageId(LEFT_LANGUAGE_ID);
        if (flashcard.dto().getFrontContent() instanceof FlashcardContentUploadVideoDto videoDto)
            videoDto.setLanguageId(LEFT_LANGUAGE_ID);

        if (flashcard.dto().getBackContent() instanceof FlashcardContentTextDto textContent)
            textContent.setLanguageId(RIGHT_LANGUAGE_ID);
        if (flashcard.dto().getBackContent() instanceof FlashcardContentUploadAudioDto audioDto)
            audioDto.setLanguageId(RIGHT_LANGUAGE_ID);
        if (flashcard.dto().getBackContent() instanceof FlashcardContentUploadVideoDto videoDto)
            videoDto.setLanguageId(RIGHT_LANGUAGE_ID);

        return flashcard;
    }

    static Stream<Arguments> provideFlashcardContentCombinations() {
        var allContentTypes = new ArrayList<String>();
        allContentTypes.add(ContentTypes.TEXT);
        allContentTypes.add(ContentTypes.AUDIO_UPLOAD);
        allContentTypes.add(ContentTypes.IMAGE_UPLOAD);
        allContentTypes.add(ContentTypes.VIDEO_UPLOAD);

        // All combinations of content types (left side, right side)
        return allContentTypes.stream()
                .flatMap(leftType -> allContentTypes.stream()
                        .map(rightType -> Arguments.of(leftType, rightType)));
    }

    @TestConfiguration
    static class TestConfig extends IntegrationTestConfigurations.Default { }
}

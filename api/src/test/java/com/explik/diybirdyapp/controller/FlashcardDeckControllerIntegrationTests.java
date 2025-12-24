package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.IntegrationTestConfigurations;
import com.explik.diybirdyapp.TestDataService;
import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FlashcardDeckControllerIntegrationTests {
    @Autowired
    FlashcardDeckController controller;

    @Autowired
    TestDataService testDataService;

    @BeforeEach
    void setup() {
        testDataService.clear();
    }

//    // POST /api/flashcard-deck tests
//    @Test
//    void givenDto_whenCreate_thenReturnMatchingDto() {
//        var dto = createDeck();
//
//        var created = controller.create(dto);
//
//        assertNotNull(created);
//        assertEquals(dto.getId(), created.getId());
//        assertEquals(dto.getName(), created.getName());
//    }
//
//    @Test
//    void givenDtoWithoutId_whenCreate_thenReturnDtoWithId() {
//        var dto = createDeck();
//        dto.setId(null); // id is optional
//
//        var created = controller.create(dto);
//
//        assertNotNull(created);
//        assertNotNull(created.getId());
//    }
//
//    // GET /api/flashcard-deck/{id} tests
//    @Test
//    void givenNewlyCreatedDeck_whenGetById_thenReturnDeck() {
//        var dto = createDeck();
//        var created = controller.create(dto);
//
//        var found = controller.get(created.getId());
//
//        assertNotNull(found);
//        assertEquals(created.getId(), found.getId());
//        assertEquals(created.getName(), found.getName());
//    }
//
//    @Test
//    void givenNonExistentDeck_whenGet_thenThrowsException() {
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> controller.get("Non-existent id"));
//    }
//
//    // GET /api/flashcard-deck tests
//    @Test
//    void givenNothing_whenGetAll_thenReturnEmptyList() {
//        var all = controller.getAll();
//
//        assertNotNull(all);
//        assertTrue(all.isEmpty());
//    }
//
//    @Test
//    void givenOneNewlyCreatedDeck_whenGetAll_thenReturnListWithOneDeck() {
//        controller.create(createDeck());
//
//        var all = controller.getAll();
//
//        assertNotNull(all);
//        assertEquals(1, all.size());
//    }
//
//    @Test
//    void givenTwoNewlyCreatedDecks_whenGetAll_thenReturnListWithTwoDecks() {
//        controller.create(createDeck());
//        controller.create(createDeck());
//
//        var all = controller.getAll();
//
//        assertNotNull(all);
//        assertEquals(2, all.size());
//    }
//
//    // PUT /api/flashcard-deck tests
//    @Test
//    void givenNewlyCreatedDeck_whenUpdate_thenReturnUpdatedDeck() {
//        var dto = createDeck();
//        var created = controller.create(dto);
//
//        var change = createDeckChange(created.getId());
//        change.setName("Updated name");
//
//        var updated = controller.update(change);
//
//        assertNotNull(updated);
//        assertEquals(change.getId(), updated.getId());
//        assertEquals(change.getName(), updated.getName());
//    }
//
//    // PUT /api/flashcard-deck tests
//    @Test
//    void givenNonExistentDeck_whenUpdate_thenThrowsException() {
//        var dto = createDeckChange("Non-existent id");
//        dto.setName("Updated name");
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> controller.update(dto));
//    }
//
//    // DELETE /api/flashcard-deck/{id} tests
//    @Test
//    void givenNewlyCreatedDeck_whenDelete_thenDeckIsDeleted() {
//        var dto = createDeck();
//        var created = controller.create(dto);
//
//        controller.delete(created.getId());
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> controller.get(created.getId()));
//    }
//
//    @Test
//    void givenNonExistentDeck_whenDelete_thenThrowsException() {
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> controller.delete("Non-existent id"));
//    }
//
//    private FlashcardDeckDto createDeck() {
//        var dto = new FlashcardDeckDto();
//        dto.setId("Original id");
//        dto.setName("Original name");
//        return dto;
//    }
//
//    private FlashcardDeckDto createDeckChange(String id) {
//        var dto = new FlashcardDeckDto();
//        dto.setId(id);
//        return dto;
//    }

    @TestConfiguration
    public static class TestConfig extends IntegrationTestConfigurations.Default { }
}

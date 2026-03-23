package com.explik.diybirdyapp.manager.contentCrawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnpracticedFlashcardContentCrawlerUnitTest {

    private static final String LANGUAGE_ID_DANISH = "lang-da";
    private static final String SESSION_TYPE = "flashcardDeck";
    private static final String SESSION_ID = "session-1";
    private static final String DECK_ID = "deck-1";

    private final UnpracticedFlashcardContentCrawler crawler = new UnpracticedFlashcardContentCrawler();

    @Test
    void givenNoPracticedContent_whenCrawl_thenReturnsFirstThreeFlashcardsInDeckOrder() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_DANISH);

        var flashcard1 = fixture.createFlashcard("flashcard-1", fixture.createTextContent("text-1", language), null);
        var flashcard2 = fixture.createFlashcard("flashcard-2", fixture.createTextContent("text-2", language), null);
        var flashcard3 = fixture.createFlashcard("flashcard-3", fixture.createTextContent("text-3", language), null);
        var flashcard4 = fixture.createFlashcard("flashcard-4", fixture.createTextContent("text-4", language), null);
        var deck = fixture.createDeck(DECK_ID, flashcard1, flashcard2, flashcard3, flashcard4);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, language);

        var resultIds = fixture.toIdList(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(java.util.List.of("flashcard-1", "flashcard-2", "flashcard-3"), resultIds);
    }

    @Test
    void givenFirstFlashcardAlreadyPracticed_whenCrawl_thenReturnsNextThreeFlashcardsOnly() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_DANISH);

        var flashcard1 = fixture.createFlashcard("flashcard-1", fixture.createTextContent("text-1", language), null);
        var flashcard2 = fixture.createFlashcard("flashcard-2", fixture.createTextContent("text-2", language), null);
        var flashcard3 = fixture.createFlashcard("flashcard-3", fixture.createTextContent("text-3", language), null);
        var flashcard4 = fixture.createFlashcard("flashcard-4", fixture.createTextContent("text-4", language), null);
        var deck = fixture.createDeck(DECK_ID, flashcard1, flashcard2, flashcard3, flashcard4);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, language);
        sessionState.addPracticedContent(flashcard1);

        var resultIds = fixture.toIdList(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(java.util.List.of("flashcard-2", "flashcard-3", "flashcard-4"), resultIds);
    }

    @Test
    void givenAllFlashcardsPracticed_whenCrawl_thenReturnsEmpty() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_DANISH);

        var flashcard = fixture.createFlashcard("flashcard-1", fixture.createTextContent("text-1", language), null);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, language);
        sessionState.addPracticedContent(flashcard);

        var result = crawler.crawl(fixture.params(deck, sessionState)).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void givenShuffleModeEnabled_whenCrawl_thenReturnsUpToThreeUnpracticedFlashcards() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_DANISH);

        var flashcard1 = fixture.createFlashcard("flashcard-1", fixture.createTextContent("text-1", language), null);
        var flashcard2 = fixture.createFlashcard("flashcard-2", fixture.createTextContent("text-2", language), null);
        var flashcard3 = fixture.createFlashcard("flashcard-3", fixture.createTextContent("text-3", language), null);
        var flashcard4 = fixture.createFlashcard("flashcard-4", fixture.createTextContent("text-4", language), null);
        var deck = fixture.createDeck(DECK_ID, flashcard1, flashcard2, flashcard3, flashcard4);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, true, language);

        var resultIds = fixture.toIdList(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(3, resultIds.size());
        assertTrue(resultIds.stream().allMatch(id -> id.startsWith("flashcard-")));
        assertEquals(resultIds.size(), java.util.Set.copyOf(resultIds).size()); // no duplicates
    }
}

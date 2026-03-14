package com.explik.diybirdyapp.manager.contentCrawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnpracticedFlashcardContentCrawlerUnitTest {

    private static final String LANGUAGE_ID_DANISH = "lang-da";
    private static final String LANGUAGE_ID_TARGET = "lang-target";
    private static final String LANGUAGE_ID_OTHER = "lang-other";
    private static final String SESSION_TYPE = "flashcardDeck";
    private static final String SESSION_ID = "session-1";
    private static final String DECK_ID = "deck-1";

    private final UnpracticedFlashcardContentCrawler crawler = new UnpracticedFlashcardContentCrawler();

    @Test
    void givenSequentialModeAndFirstFlashcardAlreadyActive_whenCrawl_thenReturnsNextFlashcardContent() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_DANISH);

        var firstLeft = fixture.createTextContent("text-left-1", language);
        var firstRight = fixture.createTextContent("text-right-1", language);
        var secondLeft = fixture.createTextContent("text-left-2", language);
        var secondRight = fixture.createTextContent("text-right-2", language);

        var firstFlashcard = fixture.createFlashcard("flashcard-1", firstLeft, firstRight);
        var secondFlashcard = fixture.createFlashcard("flashcard-2", secondLeft, secondRight);
        var deck = fixture.createDeck(DECK_ID, firstFlashcard, secondFlashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, language);
        sessionState.addActiveContent(firstFlashcard);

        var resultIds = fixture.toIdSet(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(java.util.Set.of("flashcard-2", "text-left-2", "text-right-2"), resultIds);
    }

    @Test
    void givenTargetLanguageConfigured_whenCrawl_thenReturnsOnlyPronunciationsMatchingTargetLanguage() {
        var fixture = new ContentCrawlerTestFixture();
        var targetLanguage = fixture.createLanguage(LANGUAGE_ID_TARGET);
        var nonTargetLanguage = fixture.createLanguage(LANGUAGE_ID_OTHER);

        var left = fixture.createTextContent("text-left", targetLanguage);
        var right = fixture.createTextContent("text-right", targetLanguage);
        var pronunciationTextTarget = fixture.createTextContent("text-pron-target", targetLanguage);
        var pronunciationTextNonTarget = fixture.createTextContent("text-pron-other", nonTargetLanguage);
        fixture.createPronunciation("pron-target", left, pronunciationTextTarget);
        fixture.createPronunciation("pron-other", left, pronunciationTextNonTarget);

        var flashcard = fixture.createFlashcard("flashcard-1", left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, targetLanguage);

        var resultIds = fixture.toIdSet(crawler.crawl(fixture.params(deck, sessionState)));

        assertTrue(resultIds.contains("pron-target"));
        assertFalse(resultIds.contains("pron-other"));
    }

    @Test
    void givenAllFlashcardsAlreadyActive_whenCrawl_thenReturnsEmptyContent() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_DANISH);

        var left = fixture.createTextContent("text-left", language);
        var right = fixture.createTextContent("text-right", language);
        var flashcard = fixture.createFlashcard("flashcard-1", left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, language);
        sessionState.addActiveContent(flashcard);

        var result = crawler.crawl(fixture.params(deck, sessionState)).toList();

        assertTrue(result.isEmpty());
    }
}

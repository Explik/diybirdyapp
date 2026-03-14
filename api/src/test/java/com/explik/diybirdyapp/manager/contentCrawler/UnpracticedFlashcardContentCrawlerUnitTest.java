package com.explik.diybirdyapp.manager.contentCrawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void givenSequentialModeAndFirstFlashcardAlreadyActive_whenCrawl_thenReturnsNextThreeFlashcardsAndTheirContent() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_DANISH);

        var firstText = fixture.createTextContent("text-1", language);
        var secondText = fixture.createTextContent("text-2", language);
        var thirdText = fixture.createTextContent("text-3", language);
        var fourthText = fixture.createTextContent("text-4", language);

        var firstFlashcard = fixture.createFlashcard("flashcard-1", firstText, null);
        var secondFlashcard = fixture.createFlashcard("flashcard-2", secondText, null);
        var thirdFlashcard = fixture.createFlashcard("flashcard-3", thirdText, null);
        var fourthFlashcard = fixture.createFlashcard("flashcard-4", fourthText, null);
        var deck = fixture.createDeck(DECK_ID, firstFlashcard, secondFlashcard, thirdFlashcard, fourthFlashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, language);
        sessionState.addActiveContent(firstFlashcard);

        var resultIds = fixture.toIdList(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(
                java.util.List.of("flashcard-2", "flashcard-3", "flashcard-4", "text-2", "text-3", "text-4"),
                resultIds);
    }

    @Test
    void givenSharedContentAcrossSelectedFlashcards_whenCrawl_thenOrdersVerticesByLayerFromSelectedSet() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_TARGET);

        var textOne = fixture.createTextContent("text-1", language);
        var textTwo = fixture.createTextContent("text-2", language);
        var textThree = fixture.createTextContent("text-3", language);
        fixture.createPronunciation("pron-1", textOne, textOne);
        fixture.createPronunciation("pron-2", textTwo, textTwo);

        var flashcardOne = fixture.createFlashcard("flashcard-1", textOne, null);
        var flashcardTwo = fixture.createFlashcard("flashcard-2", textTwo, null);
        var flashcardThree = fixture.createFlashcard("flashcard-3", textThree, null);
        var flashcardFour = fixture.createFlashcard("flashcard-4", textOne, null);
        var flashcardFive = fixture.createFlashcard("flashcard-5", textTwo, null);
        var deck = fixture.createDeck(DECK_ID, flashcardOne, flashcardTwo, flashcardThree, flashcardFour, flashcardFive);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, language);

        var resultIds = fixture.toIdList(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(
                java.util.List.of(
                        "flashcard-1",
                        "flashcard-2",
                        "flashcard-3",
                        "text-1",
                        "text-2",
                        "text-3",
                        "pron-1",
                        "pron-2",
                        "flashcard-4",
                        "flashcard-5"),
                resultIds);
    }

    @Test
    void givenTargetLanguageConfigured_whenCrawl_thenReturnsOnlyPronunciationsMatchingTargetLanguage() {
        var fixture = new ContentCrawlerTestFixture();
        var targetLanguage = fixture.createLanguage(LANGUAGE_ID_TARGET);
        var nonTargetLanguage = fixture.createLanguage(LANGUAGE_ID_OTHER);

        var left = fixture.createTextContent("text-left", targetLanguage);
        var pronunciationTextTarget = fixture.createTextContent("text-pron-target", targetLanguage);
        var pronunciationTextNonTarget = fixture.createTextContent("text-pron-other", nonTargetLanguage);
        fixture.createPronunciation("pron-target", left, pronunciationTextTarget);
        fixture.createPronunciation("pron-other", left, pronunciationTextNonTarget);

        var flashcard = fixture.createFlashcard("flashcard-1", left, null);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        fixture.createSessionWithOptions(SESSION_ID, sessionState, false, targetLanguage);

        var resultIds = fixture.toIdList(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(java.util.List.of("flashcard-1", "text-left", "pron-target"), resultIds);
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

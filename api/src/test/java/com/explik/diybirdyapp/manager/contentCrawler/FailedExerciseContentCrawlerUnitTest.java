package com.explik.diybirdyapp.manager.contentCrawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailedExerciseContentCrawlerUnitTest {

    private static final String LANGUAGE_ID = "lang-da";
    private static final String SESSION_TYPE = "flashcardDeck";
    private static final String SESSION_ID = "session-1";
    private static final String DECK_ID = "deck-1";
    private static final String FLASHCARD_ID = "flashcard-1";
    private static final String TEXT_LEFT_ID = "text-left";
    private static final String TEXT_RIGHT_ID = "text-right";
    private static final String PRONUNCIATION_LEFT_ID = "pron-left";
    private static final String EXERCISE_ID = "exercise-1";

    private final FailedExerciseContentCrawler crawler = new FailedExerciseContentCrawler();

    @Test
    void givenExercisesWithoutCalculatedErrorScore_whenCrawl_thenReturnsEmpty() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var left = fixture.createTextContent(TEXT_LEFT_ID, language);
        var right = fixture.createTextContent(TEXT_RIGHT_ID, language);
        var pronunciation = fixture.createPronunciation(PRONUNCIATION_LEFT_ID, left, left);

        var flashcard = fixture.createFlashcard(FLASHCARD_ID, left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);
        fixture.createExercise(EXERCISE_ID, session, flashcard);

        // Even with content scores set, crawler must ignore content until exercise score exists.
        fixture.setContentErrorScore(flashcard, 5.0d);
        fixture.setContentErrorScore(left, 4.0d);
        fixture.setContentErrorScore(right, 3.0d);
        fixture.setContentErrorScore(pronunciation, 2.0d);

        var result = crawler.crawl(fixture.params(deck, sessionState)).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void givenCalculatedExerciseAndContentScores_whenCrawl_thenReturnsScoredDeckContent() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var left = fixture.createTextContent(TEXT_LEFT_ID, language);
        var right = fixture.createTextContent(TEXT_RIGHT_ID, language);
        var pronunciation = fixture.createPronunciation(PRONUNCIATION_LEFT_ID, left, left);

        var flashcard = fixture.createFlashcard(FLASHCARD_ID, left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);
        var exercise = fixture.createExercise(EXERCISE_ID, session, flashcard);

        fixture.setExerciseErrorScore(exercise, 3.0d);
        fixture.setContentErrorScore(flashcard, 5.0d);
        fixture.setContentErrorScore(left, 4.0d);
        fixture.setContentErrorScore(right, 3.0d);
        fixture.setContentErrorScore(pronunciation, 2.0d);

        var resultIds = fixture.toIdSet(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(java.util.Set.of(FLASHCARD_ID, TEXT_LEFT_ID, TEXT_RIGHT_ID, PRONUNCIATION_LEFT_ID), resultIds);
    }

    @Test
    void givenScoredContentOutsideDeck_whenCrawl_thenExcludesOutsideDeckContent() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var deckLeft = fixture.createTextContent("deck-left", language);
        var deckRight = fixture.createTextContent("deck-right", language);
        var deckFlashcard = fixture.createFlashcard("flashcard-in-deck", deckLeft, deckRight);
        var deck = fixture.createDeck(DECK_ID, deckFlashcard);

        var outsideLeft = fixture.createTextContent("outside-left", language);
        var outsideRight = fixture.createTextContent("outside-right", language);
        var outsideFlashcard = fixture.createFlashcard("flashcard-outside", outsideLeft, outsideRight);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);
        var exercise = fixture.createExercise(EXERCISE_ID, session, deckFlashcard);

        fixture.setExerciseErrorScore(exercise, 2.0d);
        fixture.setContentErrorScore(deckFlashcard, 5.0d);
        fixture.setContentErrorScore(outsideFlashcard, 5.0d);
        fixture.setContentErrorScore(outsideLeft, 5.0d);
        fixture.setContentErrorScore(outsideRight, 5.0d);

        var resultIds = fixture.toIdSet(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(java.util.Set.of("flashcard-in-deck"), resultIds);
    }

    @Test
    void givenScoredContentAlreadyActive_whenCrawl_thenExcludesAlreadyActiveVertices() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var left = fixture.createTextContent(TEXT_LEFT_ID, language);
        var right = fixture.createTextContent(TEXT_RIGHT_ID, language);
        var pronunciation = fixture.createPronunciation(PRONUNCIATION_LEFT_ID, left, left);

        var flashcard = fixture.createFlashcard(FLASHCARD_ID, left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);
        var exercise = fixture.createExercise(EXERCISE_ID, session, flashcard);

        fixture.setExerciseErrorScore(exercise, 3.0d);
        fixture.setContentErrorScore(flashcard, 5.0d);
        fixture.setContentErrorScore(left, 4.0d);
        fixture.setContentErrorScore(right, 3.0d);
        fixture.setContentErrorScore(pronunciation, 2.0d);

        sessionState.addActiveContent(flashcard);
        sessionState.addActiveContent(left);
        sessionState.addActiveContent(right);
        sessionState.addActiveContent(pronunciation);

        var result = crawler.crawl(fixture.params(deck, sessionState)).toList();
        assertTrue(result.isEmpty());
    }
}

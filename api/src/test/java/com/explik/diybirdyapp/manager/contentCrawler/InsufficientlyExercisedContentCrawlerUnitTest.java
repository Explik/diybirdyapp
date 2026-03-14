package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionStateVertex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InsufficientlyExercisedContentCrawlerUnitTest {

    private static final String LANGUAGE_ID = "lang-da";
    private static final String SESSION_TYPE = "flashcardDeck";
    private static final String SESSION_ID = "session-1";
    private static final String DECK_ID = "deck-1";
    private static final String FLASHCARD_ONE_ID = "flashcard-1";
    private static final String FLASHCARD_TWO_ID = "flashcard-2";

    private final InsufficientlyExercisedContentCrawler crawler = new InsufficientlyExercisedContentCrawler();

    @Test
    void givenContentExercisedBetweenOneAndMaxMinusOneTimes_whenCrawl_thenReturnsOnlyInsufficientlyExercisedContent() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var leftOne = fixture.createTextContent("text-left-1", language);
        var rightOne = fixture.createTextContent("text-right-1", language);
        var leftTwo = fixture.createTextContent("text-left-2", language);
        var rightTwo = fixture.createTextContent("text-right-2", language);

        var flashcardOne = fixture.createFlashcard(FLASHCARD_ONE_ID, leftOne, rightOne);
        var flashcardTwo = fixture.createFlashcard(FLASHCARD_TWO_ID, leftTwo, rightTwo);
        var deck = fixture.createDeck(DECK_ID, flashcardOne, flashcardTwo);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);

        fixture.createExercise("exercise-1", session, flashcardOne);
        fixture.createExercise("exercise-2", session, flashcardOne);

        for (int i = 0; i < ExerciseSessionStateVertex.MAX_EXERCISES_PER_CONTENT; i++) {
            fixture.createExercise("exercise-max-" + i, session, flashcardTwo);
        }

        var resultIds = fixture.toIdSet(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(java.util.Set.of(FLASHCARD_ONE_ID), resultIds);
    }

    @Test
    void givenInsufficientlyExercisedContentAlreadyActive_whenCrawl_thenExcludesItFromResult() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var left = fixture.createTextContent("text-left", language);
        var right = fixture.createTextContent("text-right", language);
        var flashcard = fixture.createFlashcard(FLASHCARD_ONE_ID, left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);
        fixture.createExercise("exercise-1", session, flashcard);

        sessionState.addActiveContent(flashcard);

        var result = crawler.crawl(fixture.params(deck, sessionState)).toList();

        assertTrue(result.isEmpty());
    }
}

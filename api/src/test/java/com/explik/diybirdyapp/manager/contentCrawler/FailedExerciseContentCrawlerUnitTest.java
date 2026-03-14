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
    private static final String ANSWER_ID = "answer-1";
    private static final String FEEDBACK_ID = "feedback-1";
    private static final String FEEDBACK_STATUS_INCORRECT = "incorrect";
    private static final String FEEDBACK_TYPE_ANSWER_EVALUATION = "answer-evaluation";
    private static final String FEEDBACK_TYPE_I_WAS_CORRECT = "i-was-correct";

    private final FailedExerciseContentCrawler crawler = new FailedExerciseContentCrawler();

    @Test
    void givenExerciseWithIncorrectFeedbackAndNoIWasCorrect_whenCrawl_thenReturnsAssociatedDeckContent() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var left = fixture.createTextContent(TEXT_LEFT_ID, language);
        var right = fixture.createTextContent(TEXT_RIGHT_ID, language);
        fixture.createPronunciation(PRONUNCIATION_LEFT_ID, left, left);

        var flashcard = fixture.createFlashcard(FLASHCARD_ID, left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);
        var exercise = fixture.createExercise(EXERCISE_ID, session, flashcard);
        var answer = fixture.createAnswer(ANSWER_ID, exercise);
        fixture.createFeedback(FEEDBACK_ID, answer, FEEDBACK_STATUS_INCORRECT, FEEDBACK_TYPE_ANSWER_EVALUATION);

        var resultIds = fixture.toIdSet(crawler.crawl(fixture.params(deck, sessionState)));

        assertEquals(java.util.Set.of(FLASHCARD_ID, TEXT_LEFT_ID, TEXT_RIGHT_ID, PRONUNCIATION_LEFT_ID), resultIds);
    }

    @Test
    void givenIncorrectFeedbackAndIWasCorrectFeedback_whenCrawl_thenExerciseIsNotTreatedAsFailed() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var left = fixture.createTextContent(TEXT_LEFT_ID, language);
        var right = fixture.createTextContent(TEXT_RIGHT_ID, language);
        var flashcard = fixture.createFlashcard(FLASHCARD_ID, left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);
        var exercise = fixture.createExercise(EXERCISE_ID, session, flashcard);
        var answer = fixture.createAnswer(ANSWER_ID, exercise);
        fixture.createFeedback(FEEDBACK_ID, answer, FEEDBACK_STATUS_INCORRECT, FEEDBACK_TYPE_ANSWER_EVALUATION);
        fixture.createFeedback("feedback-2", answer, FEEDBACK_STATUS_INCORRECT, FEEDBACK_TYPE_I_WAS_CORRECT);

        var result = crawler.crawl(fixture.params(deck, sessionState)).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void givenFailedExerciseForContentOutsideDeck_whenCrawl_thenExcludesThatContent() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID);

        var deckFlashcard = fixture.createFlashcard(
                "flashcard-in-deck",
                fixture.createTextContent("deck-left", language),
                fixture.createTextContent("deck-right", language));
        var outsideFlashcard = fixture.createFlashcard(
                "flashcard-outside",
                fixture.createTextContent("outside-left", language),
                fixture.createTextContent("outside-right", language));

        var deck = fixture.createDeck(DECK_ID, deckFlashcard);

        var sessionState = fixture.createSessionState(SESSION_TYPE);
        var session = fixture.createSessionWithoutOptions(SESSION_ID, sessionState);
        var exercise = fixture.createExercise(EXERCISE_ID, session, outsideFlashcard);
        var answer = fixture.createAnswer(ANSWER_ID, exercise);
        fixture.createFeedback(FEEDBACK_ID, answer, FEEDBACK_STATUS_INCORRECT, FEEDBACK_TYPE_ANSWER_EVALUATION);

        var result = crawler.crawl(fixture.params(deck, sessionState)).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void givenFailedExerciseContentAlreadyActive_whenCrawl_thenExcludesAlreadyActiveVertices() {
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
        var answer = fixture.createAnswer(ANSWER_ID, exercise);
        fixture.createFeedback(FEEDBACK_ID, answer, FEEDBACK_STATUS_INCORRECT, FEEDBACK_TYPE_ANSWER_EVALUATION);

        sessionState.addActiveContent(flashcard);
        sessionState.addActiveContent(left);
        sessionState.addActiveContent(right);
        sessionState.addActiveContent(pronunciation);

        var result = crawler.crawl(fixture.params(deck, sessionState)).toList();

        assertTrue(result.isEmpty());
    }
}

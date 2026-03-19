package com.explik.diybirdyapp.manager.contentCrawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailedExerciseErrorScoreEvaluatorUnitTest {

    private static final String LANGUAGE_ID = "lang-da";
    private static final String SESSION_TYPE = "flashcardDeck";
    private static final String SESSION_ID = "session-1";
    private static final String DECK_ID = "deck-1";
    private static final String FLASHCARD_ID = "flashcard-1";
    private static final String TEXT_LEFT_ID = "text-left";
    private static final String TEXT_RIGHT_ID = "text-right";
    private static final String EXERCISE_ID = "exercise-1";
    private static final String ANSWER_ID = "answer-1";

    private final FailedExerciseErrorScoreEvaluator evaluator = new FailedExerciseErrorScoreEvaluator();

    @Test
    void givenIncorrectFeedback_whenEvaluate_thenCachesExerciseAndContentScores() {
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
        fixture.createFeedback("feedback-1", answer, "incorrect", "general");

        evaluator.evaluate(deck, sessionState);

        var exerciseScore = fixture.getNumericProperty(exercise, FailedExerciseErrorScoreEvaluator.PROPERTY_EXERCISE_ERROR_SCORE);
        var flashcardScore = fixture.getNumericProperty(flashcard, FailedExerciseErrorScoreEvaluator.PROPERTY_CONTENT_ERROR_SCORE);

        assertNotNull(exerciseScore);
        assertNotNull(flashcardScore);
        assertTrue(exerciseScore > 0.0d);
        assertTrue(flashcardScore > 0.0d);
    }

    @Test
    void givenPreviousScores_whenEvaluateAgain_thenPreviousValuesAreWipedBeforeRecalculation() {
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
        fixture.createFeedback("feedback-1", answer, "incorrect", "general");

        evaluator.evaluate(deck, sessionState);

        assertTrue(fixture.hasProperty(exercise, FailedExerciseErrorScoreEvaluator.PROPERTY_EXERCISE_ERROR_SCORE));
        assertTrue(fixture.hasProperty(flashcard, FailedExerciseErrorScoreEvaluator.PROPERTY_CONTENT_ERROR_SCORE));

        // Remove scorable feedback and verify next evaluation wipes previous cache values.
        fixture.traversalSource.V().hasLabel("exerciseFeedback").drop().iterate();

        evaluator.evaluate(deck, sessionState);

        assertNull(fixture.getNumericProperty(exercise, FailedExerciseErrorScoreEvaluator.PROPERTY_EXERCISE_ERROR_SCORE));
        assertNull(fixture.getNumericProperty(flashcard, FailedExerciseErrorScoreEvaluator.PROPERTY_CONTENT_ERROR_SCORE));
    }

    @Test
    void givenIWasCorrectFeedback_whenEvaluate_thenTreatsAnswerAsCorrectSignal() {
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

        fixture.createFeedback("feedback-incorrect", answer, "incorrect", "general");
        fixture.createFeedback("feedback-i-was-correct", answer, "incorrect", "i-was-correct");

        evaluator.evaluate(deck, sessionState);

        var exerciseScore = fixture.getNumericProperty(exercise, FailedExerciseErrorScoreEvaluator.PROPERTY_EXERCISE_ERROR_SCORE);
        assertNotNull(exerciseScore);
        assertTrue(exerciseScore < 0.0d);
    }
}

package com.explik.diybirdyapp.manager.contentCrawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlashcardDeckContentCrawlerUnitTest {

    private static final String DECK_ID = "deck-1";
    private static final String LANGUAGE_ID_DANISH = "lang-da";
    private static final String LANGUAGE_ID_ENGLISH = "lang-en";

    private final FlashcardDeckContentCrawler crawler = new FlashcardDeckContentCrawler();

    @Test
    void givenFlashcardWithAssociatedContent_whenCrawl_thenReturnsFlashcardSidesAndPronunciations() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_DANISH);

        var left = fixture.createTextContent("text-left-1", language);
        var right = fixture.createTextContent("text-right-1", language);
        fixture.createPronunciation("pron-left-1", left, left);
        fixture.createPronunciation("pron-right-1", right, right);

        var flashcard = fixture.createFlashcard("flashcard-1", left, right);
        var deck = fixture.createDeck(DECK_ID, flashcard);

        var resultIds = fixture.toIdSet(crawler.crawl(deck));

        assertEquals(
                java.util.Set.of("flashcard-1", "text-left-1", "text-right-1", "pron-left-1", "pron-right-1"),
                resultIds);
    }

    @Test
    void givenSharedContentAcrossFlashcards_whenCrawl_thenReturnsEachVertexOnlyOnce() {
        var fixture = new ContentCrawlerTestFixture();
        var language = fixture.createLanguage(LANGUAGE_ID_ENGLISH);

        var sharedLeft = fixture.createTextContent("text-shared", language);
        fixture.createPronunciation("pron-shared", sharedLeft, sharedLeft);

        var rightOne = fixture.createTextContent("text-right-1", language);
        var rightTwo = fixture.createTextContent("text-right-2", language);

        var flashcardOne = fixture.createFlashcard("flashcard-1", sharedLeft, rightOne);
        var flashcardTwo = fixture.createFlashcard("flashcard-2", sharedLeft, rightTwo);
        var deck = fixture.createDeck(DECK_ID, flashcardOne, flashcardTwo);

        var resultIds = fixture.toIdList(crawler.crawl(deck));

        assertEquals(1, resultIds.stream().filter("text-shared"::equals).count());
        assertEquals(1, resultIds.stream().filter("pron-shared"::equals).count());
    }
}

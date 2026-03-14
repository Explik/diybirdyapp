package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PrioritizedFlashcardContentCrawlerUnitTest {

    private FailedExerciseContentCrawler failedContentCrawler;
    private InsufficientlyExercisedContentCrawler insufficientlyExercisedContentCrawler;
    private UnpracticedFlashcardContentCrawler unpracticedFlashcardContentCrawler;
    private PrioritizedFlashcardContentCrawler crawler;

    @BeforeEach
    void setUp() {
        failedContentCrawler = mock(FailedExerciseContentCrawler.class);
        insufficientlyExercisedContentCrawler = mock(InsufficientlyExercisedContentCrawler.class);
        unpracticedFlashcardContentCrawler = mock(UnpracticedFlashcardContentCrawler.class);

        crawler = new PrioritizedFlashcardContentCrawler(
                failedContentCrawler,
                insufficientlyExercisedContentCrawler,
                unpracticedFlashcardContentCrawler);
    }

    @Test
    void givenFailedContentAvailable_whenCrawl_thenReturnsFailedContentOnlyAndLimitsBatchToThree() {
        var params = new FlashcardDeckSessionParams(null, null);
        var failedContent = mockVertices(4);

        when(failedContentCrawler.crawl(params)).thenReturn(failedContent.stream());

        var result = crawler.crawl(params).toList();

        assertEquals(failedContent.subList(0, 3), result);
        verify(failedContentCrawler).crawl(params);
        verifyNoInteractions(insufficientlyExercisedContentCrawler);
        verifyNoInteractions(unpracticedFlashcardContentCrawler);
    }

    @Test
    void givenNoFailedContentAndInsufficientContentExists_whenCrawl_thenFallsBackToInsufficientContent() {
        var params = new FlashcardDeckSessionParams(null, null);
        var insufficientContent = mockVertices(2);

        when(failedContentCrawler.crawl(params)).thenReturn(Stream.empty());
        when(insufficientlyExercisedContentCrawler.crawl(params)).thenReturn(insufficientContent.stream());

        var result = crawler.crawl(params).toList();

        assertEquals(insufficientContent, result);
        verify(failedContentCrawler).crawl(params);
        verify(insufficientlyExercisedContentCrawler).crawl(params);
        verifyNoInteractions(unpracticedFlashcardContentCrawler);
    }

    @Test
    void givenOnlyUnpracticedContentAvailable_whenCrawl_thenUsesLastFallbackAndLimitsBatchToThree() {
        var params = new FlashcardDeckSessionParams(null, null);
        var unpracticedContent = mockVertices(4);

        when(failedContentCrawler.crawl(params)).thenReturn(Stream.empty());
        when(insufficientlyExercisedContentCrawler.crawl(params)).thenReturn(Stream.empty());
        when(unpracticedFlashcardContentCrawler.crawl(params)).thenReturn(unpracticedContent.stream());

        var result = crawler.crawl(params).toList();

        assertEquals(unpracticedContent.subList(0, 3), result);
        verify(failedContentCrawler).crawl(params);
        verify(insufficientlyExercisedContentCrawler).crawl(params);
        verify(unpracticedFlashcardContentCrawler).crawl(params);
    }

    private List<AbstractVertex> mockVertices(int count) {
        var vertices = new ArrayList<AbstractVertex>();
        for (int i = 0; i < count; i++) {
            vertices.add(mock(AbstractVertex.class));
        }
        return vertices;
    }
}

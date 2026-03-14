package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * Prioritized flashcard content crawler.
 *
 * Selects content using a strict fallback chain:
 * 1. Failed exercise content
 * 2. Insufficiently exercised content
 * 3. New unpracticed flashcard content
 */
@Component
public class PrioritizedFlashcardContentCrawler implements ContentCrawler<FlashcardDeckSessionParams> {

    private static final int MAX_CONTENT_PER_BATCH = 3;

    private final FailedExerciseContentCrawler failedContentCrawler;
    private final InsufficientlyExercisedContentCrawler insufficientlyExercisedContentCrawler;
    private final UnpracticedFlashcardContentCrawler unpracticedFlashcardContentCrawler;

    @Autowired
    public PrioritizedFlashcardContentCrawler(
            FailedExerciseContentCrawler failedContentCrawler,
            InsufficientlyExercisedContentCrawler insufficientlyExercisedContentCrawler,
            UnpracticedFlashcardContentCrawler unpracticedFlashcardContentCrawler) {
        this.failedContentCrawler = failedContentCrawler;
        this.insufficientlyExercisedContentCrawler = insufficientlyExercisedContentCrawler;
        this.unpracticedFlashcardContentCrawler = unpracticedFlashcardContentCrawler;
    }

    @Override
    public Stream<AbstractVertex> crawl(FlashcardDeckSessionParams params) {
        var failedContent = failedContentCrawler.crawl(params).limit(MAX_CONTENT_PER_BATCH).toList();
        if (!failedContent.isEmpty()) {
            return failedContent.stream();
        }

        var insufficientContent = insufficientlyExercisedContentCrawler
                .crawl(params)
                .limit(MAX_CONTENT_PER_BATCH)
                .toList();
        if (!insufficientContent.isEmpty()) {
            return insufficientContent.stream();
        }

        return unpracticedFlashcardContentCrawler.crawl(params).limit(MAX_CONTENT_PER_BATCH);
    }
}
package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    private static final int MAX_RETRY_CONTENT_PER_BATCH = 3;

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
        return firstNonEmptyStream(
                () -> failedContentCrawler.crawl(params).limit(MAX_RETRY_CONTENT_PER_BATCH),
                () -> insufficientlyExercisedContentCrawler.crawl(params).limit(MAX_RETRY_CONTENT_PER_BATCH),
                () -> unpracticedFlashcardContentCrawler.crawl(params));
    }

    @SafeVarargs
    private Stream<AbstractVertex> firstNonEmptyStream(Supplier<Stream<AbstractVertex>>... suppliers) {
        for (Supplier<Stream<AbstractVertex>> supplier : suppliers) {
            Stream<AbstractVertex> candidate = supplier.get();
            Iterator<AbstractVertex> iterator = candidate.iterator();

            if (iterator.hasNext()) {
                AbstractVertex first = iterator.next();
                Stream<AbstractVertex> remaining = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                        false);

                return Stream.concat(Stream.of(first), remaining)
                        .onClose(candidate::close);
            }

            candidate.close();
        }

        return Stream.empty();
    }
}

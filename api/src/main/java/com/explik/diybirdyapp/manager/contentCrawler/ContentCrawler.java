package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;

import java.util.stream.Stream;

/**
 * Generic content crawler interface.
 * Implementations retrieve content vertices given a parameter object of type T.
 * The returned stream is lazy: consumers that stop early (e.g. via limit or findFirst)
 * will not force more items to be produced than necessary.
 *
 * @param <T> the type of the input parameter object
 */
public interface ContentCrawler<T> {
    Stream<AbstractVertex> crawl(T params);
}

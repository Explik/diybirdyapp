package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;

import java.util.List;
import java.util.stream.Stream;

/**
 * Generic grouped content crawler interface.
 *
 * Implementations return content as groups where each list contains related
 * vertices that should be evaluated together during exercise selection.
 *
 * @param <T> the type of input parameter object
 */
public interface GroupedContentCrawler<T> {
    Stream<List<AbstractVertex>> crawlGroups(T params);
}
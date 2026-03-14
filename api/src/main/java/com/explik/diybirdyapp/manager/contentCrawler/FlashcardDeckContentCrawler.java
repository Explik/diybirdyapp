package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * Content Crawler - Retrieves all content associated with a flashcard deck.
 * 
 * Collects all flashcards and their associated content (left/right content, pronunciations, etc.) 
 * from the entire deck. Used primarily for generating options in multiple choice exercises.
 */
@Component
public class FlashcardDeckContentCrawler implements ContentCrawler<FlashcardDeckVertex> {
    
    /**
     * Collects all content from all flashcards in the deck.
     *
     * @param flashcardDeck the flashcard deck to crawl
     * @return stream of all AbstractVertex including all flashcards and their associated content
     */
    @Override
    public Stream<AbstractVertex> crawl(FlashcardDeckVertex flashcardDeck) {
        List<AbstractVertex> contentList = new ArrayList<>();
        Set<String> addedVertexIds = new HashSet<>();

        for (FlashcardVertex flashcard : flashcardDeck.getFlashcards()) {
            collectFlashcardContent(flashcard, contentList, addedVertexIds);
        }

        return contentList.stream();
    }
    
    /**
     * Collects all content related to a flashcard including the flashcard itself, its left/right content,
     * and associated content like pronunciations.
     * 
     * @param flashcardVertex The flashcard to collect content for
     * @param contentList The list to add collected content to
     * @param addedVertexIds Set of vertex IDs already added to avoid duplicates
     */
    private void collectFlashcardContent(
            FlashcardVertex flashcardVertex,
            List<AbstractVertex> contentList,
            Set<String> addedVertexIds) {
        
        // Add the flashcard itself
        if (addedVertexIds.add(flashcardVertex.getId())) {
            contentList.add(flashcardVertex);
        }
        
        // Add left content and its pronunciations
        ContentVertex leftContent = flashcardVertex.getLeftContent();
        if (leftContent != null && addedVertexIds.add(leftContent.getId())) {
            contentList.add(leftContent);
            
            if (leftContent instanceof TextContentVertex textContent) {
                for (PronunciationVertex pronunciation : textContent.getPronunciations()) {
                    if (addedVertexIds.add(pronunciation.getId())) {
                        contentList.add(pronunciation);
                    }
                }
            }
        }
        
        // Add right content and its pronunciations
        ContentVertex rightContent = flashcardVertex.getRightContent();
        if (rightContent != null && addedVertexIds.add(rightContent.getId())) {
            contentList.add(rightContent);
            
            if (rightContent instanceof TextContentVertex textContent) {
                for (PronunciationVertex pronunciation : textContent.getPronunciations()) {
                    if (addedVertexIds.add(pronunciation.getId())) {
                        contentList.add(pronunciation);
                    }
                }
            }
        }
    }
}

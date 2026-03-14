package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Unpracticed Content Crawler - Retrieves unpracticed flashcard content from a deck.
 * 
 * Collects content for the next flashcard that hasn't been added to activeContent yet.
 * Supports both sequential (chronological) and shuffled flashcard selection.
 */
@Component
public class UnpracticedFlashcardContentCrawler implements ContentCrawler<FlashcardDeckSessionParams> {
    
    /**
     * Collects content for the next flashcard that hasn't been added to activeContent yet.
     * Returns one flashcard and all its content and associated content per call.
     * Each vertex is returned only once - if it already exists in activeContent, it won't be returned again.
     * 
     * The order of flashcards is determined by the shuffleFlashcards setting:
     * - If shuffleFlashcards is false (default): flashcards are returned in the order they appear in the deck
     * - If shuffleFlashcards is true: flashcards are returned in random order
     * 
     * @param flashcardDeck The flashcard deck to crawl
     * @param sessionState The session state containing activeContent to check for duplicates
     * @return Stream of AbstractVertex including one flashcard, its content, and associated content (Pronunciation)
     *         Returns empty stream if all flashcards have been processed
     */
    @Override
    public Stream<AbstractVertex> crawl(FlashcardDeckSessionParams params) {
        return collectNextFlashcardContent(params.flashcardDeck(), params.sessionState());
    }

    private Stream<AbstractVertex> collectNextFlashcardContent(
            FlashcardDeckVertex flashcardDeck,
            ExerciseSessionStateVertex sessionState) {

        Set<String> activeVertexIds = sessionState.getActiveContent().stream()
                .map(this::getVertexId)
                .filter(Objects::nonNull)
                .collect(HashSet::new, Set::add, Set::addAll);

        // Get the session to check shuffle setting and target language
        ExerciseSessionVertex session = sessionState.getSession();
        ExerciseSessionOptionsVertex options = session.getOptions();
        boolean shuffleFlashcards = options != null && options.getShuffleFlashcards();

        var targetLanguage = options != null ? options.getTargetLanguage() : null;
        String targetLanguageId = targetLanguage != null ? targetLanguage.getId() : null;

        FlashcardVertex targetFlashcard = findTargetFlashcard(
                flashcardDeck.getFlashcards(),
                activeVertexIds,
                shuffleFlashcards);

        // If no new flashcard is found, return an empty stream.
        if (targetFlashcard == null) {
            return Stream.empty();
        }

        Set<String> emittedVertexIds = new HashSet<>();
        return streamFlashcardContent(targetFlashcard, targetLanguageId)
                .filter(vertex -> includeVertex(vertex, activeVertexIds, emittedVertexIds));
    }

    private FlashcardVertex findTargetFlashcard(
            List<? extends FlashcardVertex> flashcards,
            Set<String> activeVertexIds,
            boolean shuffleFlashcards) {

        if (!shuffleFlashcards) {
            return flashcards.stream()
                    .filter(flashcard -> !activeVertexIds.contains(flashcard.getId()))
                    .findFirst()
                    .orElse(null);
        }

        // Reservoir sampling keeps random selection uniform without allocating an intermediate list.
        FlashcardVertex chosen = null;
        int seen = 0;

        for (FlashcardVertex flashcard : flashcards) {
            if (activeVertexIds.contains(flashcard.getId())) {
                continue;
            }

            seen++;
            if (ThreadLocalRandom.current().nextInt(seen) == 0) {
                chosen = flashcard;
            }
        }

        return chosen;
    }

    /**
     * Collects all content related to a flashcard including the flashcard itself, its left/right content,
     * and associated content like pronunciations. Excludes vertices already in the active set.
     * Filters pronunciations by target language if specified.
     *
     * @param flashcardVertex The flashcard to collect content for
     * @param targetLanguageId Target language ID to filter pronunciations (null = all languages)
     * @return Stream of AbstractVertex including flashcard, content, and associated content (Pronunciation)
     */
    private Stream<AbstractVertex> streamFlashcardContent(
            FlashcardVertex flashcardVertex,
            String targetLanguageId) {

        return Stream.concat(
                Stream.of(flashcardVertex),
                Stream.of(flashcardVertex.getLeftContent(), flashcardVertex.getRightContent())
                        .filter(Objects::nonNull)
                        .flatMap(content -> streamContentAndAssociations(content, targetLanguageId)));
    }

    private Stream<AbstractVertex> streamContentAndAssociations(ContentVertex content, String targetLanguageId) {
        if (!(content instanceof TextContentVertex textContent)) {
            return Stream.of(content);
        }

        Stream<AbstractVertex> pronunciationStream = textContent.getPronunciations().stream()
                .filter(pronunciation -> targetLanguageId != null && matchesTargetLanguage(pronunciation, targetLanguageId))
                .map(pronunciation -> (AbstractVertex) pronunciation);

        return Stream.concat(Stream.of(content), pronunciationStream);
    }

    private boolean includeVertex(
            AbstractVertex vertex,
            Set<String> activeVertexIds,
            Set<String> emittedVertexIds) {

        String vertexId = getVertexId(vertex);
        return vertexId != null
                && !activeVertexIds.contains(vertexId)
                && emittedVertexIds.add(vertexId);
    }
    
    /**
     * Checks if a pronunciation matches the target language.
     * 
     * @param pronunciation The pronunciation vertex to check
     * @param targetLanguageId The target language ID
     * @return True if the pronunciation's text content matches the target language
     */
    private boolean matchesTargetLanguage(PronunciationVertex pronunciation, String targetLanguageId) {
        var textContent = pronunciation.getTextContent();
        if (textContent == null) {
            return false;
        }
        var language = textContent.getLanguage();
        return language != null && targetLanguageId.equals(language.getId());
    }

    /**
     * Gets the ID from a vertex, handling both ContentVertex and PronunciationVertex types.
     * 
     * @param vertex The vertex to get the ID from
     * @return The vertex ID, or null if it cannot be determined
     */
    private String getVertexId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        } else if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        }
        return null;
    }
}

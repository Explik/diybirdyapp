package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * Unpracticed Content Crawler - Retrieves unpracticed flashcard content from a deck.
 *
 * Collects content for the next three unpracticed flashcards and nearby associated vertices.
 * Supports both sequential (chronological) and shuffled flashcard selection.
 */
@Component
public class UnpracticedFlashcardContentCrawler implements ContentCrawler<FlashcardDeckSessionParams> {

    private static final int ROOT_FLASHCARD_BATCH_SIZE = 3;

    /**
     * Collects content for the next three flashcards that haven't been added to activeContent yet.
     * Returns the selected flashcards first, then their direct content, then pronunciations,
     * and finally any additional flashcards that share the selected content.
     * Each vertex is returned only once - if it already exists in activeContent, it won't be returned again.
     *
     * The order of flashcards is determined by the shuffleFlashcards setting:
     * - If shuffleFlashcards is false (default): flashcards are returned in the order they appear in the deck
     * - If shuffleFlashcards is true: flashcards are returned in random order
     *
     * @param flashcardDeck The flashcard deck to crawl
     * @param sessionState The session state containing activeContent to check for duplicates
     * @return Stream of AbstractVertex including up to three flashcards, their content, pronunciations,
     *         and related flashcards that share the selected content
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

        ExerciseSessionVertex session = sessionState.getSession();
        ExerciseSessionOptionsVertex options = session != null ? session.getOptions() : null;
        boolean shuffleFlashcards = options != null && options.getShuffleFlashcards();

        var targetLanguage = options != null ? options.getTargetLanguage() : null;
        String targetLanguageId = targetLanguage != null ? targetLanguage.getId() : null;

        List<? extends FlashcardVertex> deckFlashcards = flashcardDeck.getFlashcards();
        Map<String, Integer> deckOrderByFlashcardId = buildDeckOrderByFlashcardId(deckFlashcards);
        List<FlashcardVertex> targetFlashcards = findTargetFlashcards(deckFlashcards, activeVertexIds, shuffleFlashcards);

        if (targetFlashcards.isEmpty()) {
            return Stream.empty();
        }

        List<ContentVertex> rootContents = collectRootContents(targetFlashcards);
        Set<String> emittedVertexIds = new HashSet<>();
        List<AbstractVertex> orderedVertices = new ArrayList<>();

        appendVertices(orderedVertices, targetFlashcards, activeVertexIds, emittedVertexIds);
        appendVertices(orderedVertices, rootContents, activeVertexIds, emittedVertexIds);
        appendVertices(orderedVertices, collectPronunciations(rootContents, targetLanguageId), activeVertexIds, emittedVertexIds);
        appendVertices(
                orderedVertices,
                collectRelatedFlashcards(rootContents, targetFlashcards, deckOrderByFlashcardId),
                activeVertexIds,
                emittedVertexIds);

        return orderedVertices.stream();
    }

    private Map<String, Integer> buildDeckOrderByFlashcardId(List<? extends FlashcardVertex> flashcards) {
        Map<String, Integer> deckOrderByFlashcardId = new HashMap<>();

        for (int index = 0; index < flashcards.size(); index++) {
            FlashcardVertex flashcard = flashcards.get(index);
            if (flashcard.getId() != null) {
                deckOrderByFlashcardId.putIfAbsent(flashcard.getId(), index);
            }
        }

        return deckOrderByFlashcardId;
    }

    private List<FlashcardVertex> findTargetFlashcards(
            List<? extends FlashcardVertex> flashcards,
            Set<String> activeVertexIds,
            boolean shuffleFlashcards) {

        List<FlashcardVertex> candidates = new ArrayList<>();

        for (FlashcardVertex flashcard : flashcards) {
            String flashcardId = flashcard.getId();
            if (flashcardId != null && !activeVertexIds.contains(flashcardId)) {
                candidates.add(flashcard);
            }
        }

        if (shuffleFlashcards) {
            Collections.shuffle(candidates);
        }

        if (candidates.size() <= ROOT_FLASHCARD_BATCH_SIZE) {
            return candidates;
        }

        return new ArrayList<>(candidates.subList(0, ROOT_FLASHCARD_BATCH_SIZE));
    }

    private List<ContentVertex> collectRootContents(List<FlashcardVertex> flashcards) {
        List<ContentVertex> rootContents = new ArrayList<>();
        Set<String> seenVertexIds = new HashSet<>();

        for (FlashcardVertex flashcard : flashcards) {
            appendDistinctVertex(rootContents, seenVertexIds, flashcard.getLeftContent());
            appendDistinctVertex(rootContents, seenVertexIds, flashcard.getRightContent());
        }

        return rootContents;
    }

    private List<PronunciationVertex> collectPronunciations(List<ContentVertex> rootContents, String targetLanguageId) {
        List<PronunciationVertex> pronunciations = new ArrayList<>();
        Set<String> seenVertexIds = new HashSet<>();

        for (ContentVertex content : rootContents) {
            if (!(content instanceof TextContentVertex textContent)) {
                continue;
            }

            for (PronunciationVertex pronunciation : textContent.getPronunciations()) {
                if (targetLanguageId != null && matchesTargetLanguage(pronunciation, targetLanguageId)) {
                    appendDistinctVertex(pronunciations, seenVertexIds, pronunciation);
                }
            }
        }

        return pronunciations;
    }

    private List<FlashcardVertex> collectRelatedFlashcards(
            List<ContentVertex> rootContents,
            List<FlashcardVertex> rootFlashcards,
            Map<String, Integer> deckOrderByFlashcardId) {

        Set<String> rootFlashcardIds = rootFlashcards.stream()
                .map(FlashcardVertex::getId)
                .filter(Objects::nonNull)
                .collect(HashSet::new, Set::add, Set::addAll);
        List<FlashcardVertex> relatedFlashcards = new ArrayList<>();
        Set<String> seenVertexIds = new HashSet<>();

        for (ContentVertex content : rootContents) {
            List<FlashcardVertex> flashcardsForContent = new ArrayList<>();
            flashcardsForContent.addAll(VertexHelper.getIngoingModels(content, FlashcardVertex.EDGE_LEFT_CONTENT, FlashcardVertex::new));
            flashcardsForContent.addAll(VertexHelper.getIngoingModels(content, FlashcardVertex.EDGE_RIGHT_CONTENT, FlashcardVertex::new));
            flashcardsForContent.sort(Comparator
                    .comparingInt((FlashcardVertex flashcard) -> deckOrderByFlashcardId.getOrDefault(flashcard.getId(), Integer.MAX_VALUE))
                    .thenComparing(flashcard -> Objects.toString(flashcard.getId(), "")));

            for (FlashcardVertex flashcard : flashcardsForContent) {
                if (!rootFlashcardIds.contains(flashcard.getId())) {
                    appendDistinctVertex(relatedFlashcards, seenVertexIds, flashcard);
                }
            }
        }

        return relatedFlashcards;
    }

    private void appendVertices(
            List<AbstractVertex> orderedVertices,
            Collection<? extends AbstractVertex> candidates,
            Set<String> activeVertexIds,
            Set<String> emittedVertexIds) {

        for (AbstractVertex vertex : candidates) {
            if (includeVertex(vertex, activeVertexIds, emittedVertexIds)) {
                orderedVertices.add(vertex);
            }
        }
    }

    private <T extends AbstractVertex> void appendDistinctVertex(
            List<T> orderedVertices,
            Set<String> seenVertexIds,
            T vertex) {

        if (vertex == null) {
            return;
        }

        String vertexId = getVertexId(vertex);
        if (vertexId != null && seenVertexIds.add(vertexId)) {
            orderedVertices.add(vertex);
        }
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
     * @return true if the pronunciation's text content matches the target language
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

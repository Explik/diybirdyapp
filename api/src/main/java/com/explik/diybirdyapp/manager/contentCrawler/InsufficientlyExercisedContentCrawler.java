package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * Insufficiently Exercised Content Crawler - Retrieves content that needs more practice.
 * 
 * The crawler takes a flashcard deck, identifies all practiced content, identifies any content 
 * that has not been sufficiently exercised and returns a subset of this content to be used in 
 * the next exercise batch. An item of content has not been sufficiently exercised if it has been 
 * exercised less than MAX_EXERCISES_PER_CONTENT times overall.
 */
@Component
public class InsufficientlyExercisedContentCrawler implements ContentCrawler<FlashcardDeckSessionParams> {
    
    /**
     * Collects content that has been practiced but needs more exercises (< MAX_EXERCISES_PER_CONTENT total).
     * Returns content from the flashcard deck that hasn't been added to activeContent yet.
     * 
     * @param flashcardDeck The flashcard deck to check against
     * @param sessionState The session state containing activeContent to check for duplicates
     * @return Stream of AbstractVertex that need more practice
     *         Returns empty stream if no insufficiently exercised content is available
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

        // Get the session to count exercises per content
        ExerciseSessionVertex session = sessionState.getSession();
        ExerciseSessionOptionsVertex options = session.getOptions();

        var targetLanguage = options != null ? options.getTargetLanguage() : null;
        String targetLanguageId = targetLanguage != null ? targetLanguage.getId() : null;

        Map<String, Integer> exerciseCounts = countExercisesByContentId(session.getExercises());
        Set<String> seenVertexIds = new HashSet<>();

        return flashcardDeck.getFlashcards().stream()
                .flatMap(flashcard -> streamFlashcardContent(flashcard, targetLanguageId))
                .filter(vertex -> includeInsufficientlyExercised(
                        vertex,
                        activeVertexIds,
                        seenVertexIds,
                        exerciseCounts));
    }

    /**
     * Counts how many exercises have been created for a specific content vertex in this session.
     *
     * @param exercises Exercises in the current session
     * @return Map keyed by content ID with total exercise references count
     */
    private Map<String, Integer> countExercisesByContentId(List<ExerciseVertex> exercises) {
        Map<String, Integer> counts = new HashMap<>();

        for (ExerciseVertex exercise : exercises) {
            // Main exercise content
            ContentVertex exerciseContent = exercise.getContent();
            if (exerciseContent != null) {
                incrementCount(counts, exerciseContent.getId());
            }

            // Based-on content
            AbstractVertex basedOnContent = exercise.getBasedOnContent();
            if (basedOnContent instanceof ContentVertex contentVertex) {
                incrementCount(counts, contentVertex.getId());
            }

            // Count each option content at most once per exercise.
            exercise.getOptions().stream()
                    .map(ContentVertex::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .forEach(optionId -> incrementCount(counts, optionId));
        }

        return counts;
    }

    private void incrementCount(Map<String, Integer> counts, String contentId) {
        if (contentId == null) {
            return;
        }
        counts.merge(contentId, 1, Integer::sum);
    }

    private Stream<AbstractVertex> streamFlashcardContent(FlashcardVertex flashcard, String targetLanguageId) {
        return Stream.concat(
                Stream.of(flashcard),
                Stream.of(flashcard.getLeftContent(), flashcard.getRightContent())
                        .filter(Objects::nonNull)
                        .flatMap(content -> streamContentAndAssociations(content, targetLanguageId)));
    }

    /**
     * Adds content and its associated content (like pronunciations) to the set.
     * Filters pronunciations by target language if specified.
     *
     * @param content The content to add
     * @param targetLanguageId Target language ID to filter pronunciations (null = all languages)
     */
    private Stream<AbstractVertex> streamContentAndAssociations(ContentVertex content, String targetLanguageId) {
        if (!(content instanceof TextContentVertex textContent)) {
            return Stream.of(content);
        }

        Stream<AbstractVertex> pronunciationStream = textContent.getPronunciations().stream()
                .filter(pronunciation -> targetLanguageId == null || matchesTargetLanguage(pronunciation, targetLanguageId))
                .map(pronunciation -> (AbstractVertex) pronunciation);

        return Stream.concat(Stream.of(content), pronunciationStream);
    }

    private boolean includeInsufficientlyExercised(
            AbstractVertex vertex,
            Set<String> activeVertexIds,
            Set<String> seenVertexIds,
            Map<String, Integer> exerciseCounts) {

        String contentId = getVertexId(vertex);
        if (contentId == null || activeVertexIds.contains(contentId) || !seenVertexIds.add(contentId)) {
            return false;
        }

        int exerciseCount = exerciseCounts.getOrDefault(contentId, 0);
        return exerciseCount > 0 && exerciseCount < ExerciseSessionStateVertex.MAX_EXERCISES_PER_CONTENT;
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
     * Gets the ID from a vertex, handling different vertex types.
     * 
     * @param vertex The vertex to get the ID from
     * @return The vertex ID, or null if it cannot be determined
     */
    private String getVertexId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        } else if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        } else if (vertex instanceof FlashcardVertex flashcardVertex) {
            return flashcardVertex.getId();
        }
        return null;
    }
}

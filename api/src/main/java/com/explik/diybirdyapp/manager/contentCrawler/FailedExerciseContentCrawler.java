package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Failed Exercise Content Crawler - Retrieves content from recently failed exercises.
 * 
 * The crawler takes a flashcard deck, identifies all recently "failed" exercises, identifies 
 * any content associated with these exercises and returns a subset of this content to be used 
 * in the next exercise batch. A relevant exercise is any exercise with an answer with incorrect 
 * feedback and no "I was correct" feedback. Content is associated with an exercise if it is 
 * the main content, an answer option or etc. All returned content must be part of the flashcard 
 * deck, either flashcards or associated content.
 */
@Component
public class FailedExerciseContentCrawler implements ContentCrawler<FlashcardDeckSessionParams> {

    private static final String FEEDBACK_STATUS_INCORRECT = "incorrect";
    private static final String FEEDBACK_TYPE_I_WAS_CORRECT = "i-was-correct";
    
    /**
     * Collects content from recently failed exercises that hasn't been added to activeContent yet.
     * Returns content associated with failed exercises (flashcards, text content, pronunciations, etc.).
     * Each vertex is returned only once - if it already exists in activeContent, it won't be returned again.
     * 
     * @param flashcardDeck The flashcard deck to check against
     * @param sessionState The session state containing activeContent to check for duplicates
     * @return Stream of AbstractVertex from failed exercises that are part of the flashcard deck
     *         Returns empty stream if no failed exercise content is available
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

        Set<String> deckFlashcardIds = flashcardDeck.getFlashcards().stream()
                .map(FlashcardVertex::getId)
                .filter(Objects::nonNull)
                .collect(HashSet::new, Set::add, Set::addAll);

        Set<String> deckTextContentIds = flashcardDeck.getFlashcards().stream()
                .flatMap(this::streamFlashcardSides)
                .filter(TextContentVertex.class::isInstance)
                .map(TextContentVertex.class::cast)
                .map(TextContentVertex::getId)
                .filter(Objects::nonNull)
                .collect(HashSet::new, Set::add, Set::addAll);

        Set<String> emittedVertexIds = new HashSet<>();

        return sessionState.getSession().getExercises().stream()
                .filter(this::isFailedExercise)
                .flatMap(exercise -> streamExerciseContent(exercise, deckFlashcardIds, deckTextContentIds))
                .filter(vertex -> includeVertex(vertex, activeVertexIds, emittedVertexIds));
    }

    private Stream<ContentVertex> streamFlashcardSides(FlashcardVertex flashcard) {
        return Stream.of(flashcard.getLeftContent(), flashcard.getRightContent())
                .filter(Objects::nonNull);
    }

    private Stream<AbstractVertex> streamExerciseContent(
            ExerciseVertex exercise,
            Set<String> deckFlashcardIds,
            Set<String> deckTextContentIds) {

        Stream<ContentVertex> basedOnContent = Stream.of(exercise.getBasedOnContent())
                .filter(ContentVertex.class::isInstance)
                .map(ContentVertex.class::cast);

        Stream<ContentVertex> candidateContent = Stream.concat(
                Stream.of(exercise.getContent()),
                Stream.concat(
                        basedOnContent,
                        Stream.concat(exercise.getOptions().stream(), exercise.getCorrectOptions().stream())));

        return candidateContent
                .filter(Objects::nonNull)
                .flatMap(content -> streamContentIfInDeck(content, deckFlashcardIds, deckTextContentIds));
    }

    private Stream<AbstractVertex> streamContentIfInDeck(
            ContentVertex content,
            Set<String> deckFlashcardIds,
            Set<String> deckTextContentIds) {

        if (content instanceof FlashcardVertex flashcard) {
            if (!deckFlashcardIds.contains(flashcard.getId())) {
                return Stream.empty();
            }

            return Stream.concat(
                    Stream.of(flashcard),
                    streamFlashcardSides(flashcard).flatMap(this::streamContentAndAssociations));
        }

        if (content instanceof TextContentVertex textContent) {
            if (!deckTextContentIds.contains(textContent.getId())) {
                return Stream.empty();
            }
            return streamContentAndAssociations(textContent);
        }

        // Preserve existing behavior for non-flashcard/non-text content.
        return Stream.of(content);
    }

    private Stream<AbstractVertex> streamContentAndAssociations(ContentVertex content) {
        if (!(content instanceof TextContentVertex textContent)) {
            return Stream.of(content);
        }

        return Stream.concat(
                Stream.of(content),
                textContent.getPronunciations().stream().map(pronunciation -> (AbstractVertex) pronunciation));
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
     * Checks if an exercise is considered "failed".
     * An exercise is failed if it has at least one answer with incorrect feedback 
     * and no "I was correct" type feedback.
     *
     * @param exercise The exercise to check
     * @return true if the exercise is failed, false otherwise
     */
    private boolean isFailedExercise(ExerciseVertex exercise) {
        List<ExerciseAnswerVertex> answers = VertexHelper.getIngoingModels(
                exercise,
                ExerciseAnswerVertex.EDGE_EXERCISE,
                ExerciseAnswerVertex::new);

        return answers.stream().anyMatch(this::isFailedAnswer);
    }

    private boolean isFailedAnswer(ExerciseAnswerVertex answer) {
        List<ExerciseFeedbackVertex> feedbacks = VertexHelper.getIngoingModels(
                answer,
                ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER,
                ExerciseFeedbackVertex::new);

        boolean hasIWasCorrectFeedback = feedbacks.stream()
                .anyMatch(feedback -> FEEDBACK_TYPE_I_WAS_CORRECT.equals(feedback.getType()));

        if (hasIWasCorrectFeedback) {
            return false;
        }

        return feedbacks.stream()
                .anyMatch(feedback -> FEEDBACK_STATUS_INCORRECT.equals(feedback.getStatus()));
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
        }
        if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        }
        return null;
    }
}

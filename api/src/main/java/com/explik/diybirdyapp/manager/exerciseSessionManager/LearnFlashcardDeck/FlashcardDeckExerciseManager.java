package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.contentCrawler.FlashcardDeckContentCrawler;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
import com.explik.diybirdyapp.manager.exerciseCreationManager.MultiStageTapPairsExerciseCreationManager;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseAnswerCommandHelper;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates individual exercises for a specific content vertex and exercise type.
 * Session-level orchestration (batching, round traversal, and content selection) lives in the
 * corresponding exercise session manager.
 */
@Component
public class FlashcardDeckExerciseManager {

    private static final String FEEDBACK_STATUS_CORRECT = "correct";
    private static final String FEEDBACK_STATUS_INCORRECT = "incorrect";
    private static final String FEEDBACK_TYPE_I_WAS_CORRECT = "i-was-correct";
    private static final String FEEDBACK_PROPERTY_TYPE = "type";
    private static final String FEEDBACK_PROPERTY_STATUS = "status";
    private static final String ANSWER_PROPERTY_TYPE = "type";

    private static final String REVIEW_RATING_AGAIN = "again";
    private static final String REVIEW_RATING_HARD = "hard";

    private enum AttemptResult {
        CORRECT,
        INCORRECT,
        UNKNOWN
    }

    private static final class ExerciseAttempt {
        private final String exerciseType;
        private final AttemptResult result;

        private ExerciseAttempt(String exerciseType, AttemptResult result) {
            this.exerciseType = exerciseType;
            this.result = result;
        }

        private String getExerciseType() {
            return exerciseType;
        }

        private AttemptResult getResult() {
            return result;
        }
    }
    
    @Autowired
    private ReviewFlashcardExerciseCreationManager reviewFlashcardExerciseCreationManager;

    @Autowired
    private ViewFlashcardExerciseCreationManager viewFlashcardExerciseCreationManager;

    @Autowired
    private SelectFlashcardExerciseCreationManager selectFlashcardExerciseCreationManager;

    @Autowired
    private WriteFlashcardExerciseCreationManager writeFlashcardExerciseCreationManager;

    @Autowired
    private ListenAndSelectExerciseCreationManager listenAndSelectExerciseCreationManager;

    @Autowired
    private ListenAndWriteExerciseCreationManager listenAndWriteExerciseCreationManager;

    @Autowired
    private PronounceFlashcardExerciseCreationManager pronounceFlashcardExerciseCreationManager;

    @Autowired
    private MultiStageTapPairsExerciseCreationManager multiStageTapPairsExerciseCreationManager;

    @Autowired
    private FlashcardDeckContentCrawler deckContentCrawler;

    public ExerciseVertex createExerciseForContent(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex,
            AbstractVertex content) {

        if (sessionVertex == null || content == null) {
            return null;
        }

        var contentId = getContentId(content);
        var exerciseTypes = determineExerciseTypesForContent(
                traversalSource,
                sessionVertex,
                stateVertex,
                contentId,
                content);
        if (exerciseTypes.isEmpty()) {
            return null;
        }

        int startIndex = determineStartIndex(
                traversalSource,
                sessionVertex,
                stateVertex,
                content,
                exerciseTypes,
                contentId);

        for (int i = 0; i < exerciseTypes.size(); i++) {
            int exerciseTypeIndex = (startIndex + i) % exerciseTypes.size();
            var exerciseType = exerciseTypes.get(exerciseTypeIndex);
            var exercise = tryCreateExerciseForType(
                    traversalSource,
                    sessionVertex,
                    content,
                    exerciseType);

            if (exercise != null) {
                if (stateVertex != null && contentId != null) {
                    stateVertex.setLastExerciseTypeForContent(contentId, exerciseType);
                    if (ExerciseTypes.VIEW_FLASHCARD.equals(exerciseType)) {
                        stateVertex.setHasSeenViewExerciseForContent(contentId, true);
                    }
                }

                return exercise;
            }
        }

        return null;
    }

    private int determineStartIndex(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex,
            AbstractVertex content,
            List<String> exerciseTypes,
            String contentId) {

        int defaultStartIndex = 0;

        if (stateVertex != null && contentId != null) {
            var lastExerciseType = stateVertex.getLastExerciseTypeForContent(contentId);
            int lastExerciseTypeIndex = exerciseTypes.indexOf(lastExerciseType);
            if (lastExerciseTypeIndex >= 0) {
                defaultStartIndex = (lastExerciseTypeIndex + 1) % exerciseTypes.size();
            }
        }

        if (sessionVertex == null || content == null) {
            return defaultStartIndex;
        }

        var attemptHistory = getAttemptHistoryForContent(traversalSource, sessionVertex, content);
        if (attemptHistory.isEmpty()) {
            return defaultStartIndex;
        }

        var latestAttempt = attemptHistory.get(0);
        int latestExerciseTypeIndex = exerciseTypes.indexOf(latestAttempt.getExerciseType());
        if (latestExerciseTypeIndex < 0) {
            return defaultStartIndex;
        }

        return switch (latestAttempt.getResult()) {
            case CORRECT -> (latestExerciseTypeIndex + 1) % exerciseTypes.size();
            case INCORRECT -> {
                int consecutiveIncorrect = countConsecutiveIncorrectAttempts(
                        attemptHistory,
                        latestAttempt.getExerciseType());

                if (consecutiveIncorrect >= 2) {
                    yield Math.max(0, latestExerciseTypeIndex - 1);
                }

                yield latestExerciseTypeIndex;
            }
            case UNKNOWN -> defaultStartIndex;
        };
    }

    private List<ExerciseAttempt> getAttemptHistoryForContent(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            AbstractVertex content) {
        if (sessionVertex == null || content == null) {
            return List.of();
        }

        var contentVertexId = content.getUnderlyingVertex().id();
        List<Vertex> exerciseVertices = traversalSource.V(sessionVertex.getUnderlyingVertex())
                .outE(ExerciseSessionVertex.EDGE_EXERCISE)
                .order()
                .by(ExerciseSessionVertex.EDGE_EXERCISE_PROPERTY_CREATED_AT, Order.asc)
                .inV()
                .hasLabel(ExerciseVertex.LABEL)
                .where(__.out(ExerciseVertex.EDGE_CONTENT).hasId(contentVertexId))
                .toList();

        var attempts = new ArrayList<ExerciseAttempt>();
        for (var exerciseVertex : exerciseVertices) {
            var exercise = new ExerciseVertex(traversalSource, exerciseVertex);
            attempts.add(new ExerciseAttempt(
                    exercise.getExerciseType().getId(),
                    resolveAttemptResult(traversalSource, exerciseVertex)));
        }

        return attempts;
    }

    private int countConsecutiveIncorrectAttempts(
            List<ExerciseAttempt> attemptHistory,
            String exerciseType) {
        int count = 0;

        for (var attempt : attemptHistory) {
            if (attempt.getResult() != AttemptResult.INCORRECT) {
                break;
            }

            if (!attempt.getExerciseType().equals(exerciseType)) {
                break;
            }

            count++;
        }

        return count;
    }

    private AttemptResult resolveAttemptResult(GraphTraversalSource traversalSource, Vertex exerciseVertex) {
        boolean hasSkippedAnswer = traversalSource.V(exerciseVertex)
                .in(ExerciseAnswerVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseAnswerVertex.LABEL)
                .has(ANSWER_PROPERTY_TYPE, ExerciseAnswerCommandHelper.ANSWER_TYPE_SKIPPED)
                .hasNext();
        if (hasSkippedAnswer) {
            return AttemptResult.CORRECT;
        }

        boolean hasIWasCorrectFeedback = traversalSource.V(exerciseVertex)
                .in(ExerciseAnswerVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseAnswerVertex.LABEL)
                .in(ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER)
                .hasLabel(ExerciseFeedbackVertex.LABEL)
                .has(FEEDBACK_PROPERTY_TYPE, FEEDBACK_TYPE_I_WAS_CORRECT)
                .hasNext();
        if (hasIWasCorrectFeedback) {
            return AttemptResult.CORRECT;
        }

        boolean hasIncorrectFeedback = traversalSource.V(exerciseVertex)
                .in(ExerciseAnswerVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseAnswerVertex.LABEL)
                .in(ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER)
                .hasLabel(ExerciseFeedbackVertex.LABEL)
            .has(FEEDBACK_PROPERTY_STATUS, FEEDBACK_STATUS_INCORRECT)
                .hasNext();
        if (hasIncorrectFeedback) {
            return AttemptResult.INCORRECT;
        }

        boolean hasCorrectFeedback = traversalSource.V(exerciseVertex)
                .in(ExerciseAnswerVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseAnswerVertex.LABEL)
                .in(ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER)
                .hasLabel(ExerciseFeedbackVertex.LABEL)
            .has(FEEDBACK_PROPERTY_STATUS, FEEDBACK_STATUS_CORRECT)
                .hasNext();
        if (hasCorrectFeedback) {
            return AttemptResult.CORRECT;
        }

        var recognizabilityRating = traversalSource.V(exerciseVertex)
                .in(ExerciseAnswerVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseAnswerVertex.LABEL)
                .out(ExerciseAnswerVertex.EDGE_RECOGNIZABILITY_RATING)
                .hasLabel(RecognizabilityRatingVertex.LABEL)
                .values(RecognizabilityRatingVertex.PROPERTY_RATING)
                .tryNext();

        if (recognizabilityRating.isPresent()) {
            var rating = recognizabilityRating.get().toString();
            if (REVIEW_RATING_AGAIN.equals(rating) || REVIEW_RATING_HARD.equals(rating)) {
                return AttemptResult.INCORRECT;
            }

            return AttemptResult.CORRECT;
        }

        return AttemptResult.UNKNOWN;
    }

    private ExerciseVertex tryCreateExerciseForType(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            AbstractVertex content,
            String exerciseType) {

        FlashcardVertex flashcardVertex = null;
        PronunciationVertex pronunciationVertex = null;
        TextContentVertex textContentVertex = null;

        if (content instanceof FlashcardVertex) {
            flashcardVertex = (FlashcardVertex) content;
        } else if (content instanceof PronunciationVertex) {
            pronunciationVertex = (PronunciationVertex) content;
        } else if (content instanceof TextContentVertex) {
            textContentVertex = (TextContentVertex) content;
        } else {
            return null;
        }

        return switch (exerciseType) {
            case ExerciseTypes.REVIEW_FLASHCARD ->
                    flashcardVertex != null
                            ? tryCreateReviewExercise(traversalSource, sessionVertex, flashcardVertex)
                            : null;
            case ExerciseTypes.VIEW_FLASHCARD ->
                    flashcardVertex != null
                            ? tryCreateViewExercise(traversalSource, sessionVertex, flashcardVertex)
                            : null;
            case ExerciseTypes.SELECT_FLASHCARD ->
                    flashcardVertex != null
                            ? tryCreateSelectExercise(traversalSource, sessionVertex, flashcardVertex)
                            : null;
            case ExerciseTypes.LISTEN_AND_SELECT ->
                    pronunciationVertex != null
                            ? tryCreateListenAndSelectExercise(traversalSource, sessionVertex, pronunciationVertex)
                            : null;
            case ExerciseTypes.WRITE_FLASHCARD ->
                    flashcardVertex != null
                            ? tryCreateWriteExercise(traversalSource, sessionVertex, flashcardVertex)
                            : null;
            case ExerciseTypes.LISTEN_AND_WRITE ->
                    pronunciationVertex != null
                            ? tryCreateListenAndWriteExercise(traversalSource, sessionVertex, pronunciationVertex)
                            : null;
            case ExerciseTypes.PRONOUNCE_FLASHCARD ->
                    textContentVertex != null
                            ? tryCreatePronounceExercise(traversalSource, sessionVertex, textContentVertex)
                            : null;
            default -> null;
        };
    }

    private List<String> determineExerciseTypesForContent(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex,
            String contentId,
            AbstractVertex content) {
        var options = sessionVertex.getOptions();
        if (options == null) {
            return List.of();
        }

        var exerciseTypes = new ArrayList<String>();
        boolean hasSeenViewExercise = hasSeenViewExerciseForContent(
                traversalSource,
                sessionVertex,
                stateVertex,
                content,
                contentId);

        if (content instanceof FlashcardVertex) {
            if (options.getIncludeReviewExercises() &&
                    !hasSeenViewExercise) {
                exerciseTypes.add(ExerciseTypes.VIEW_FLASHCARD);
            }

            if (options.getIncludeMultipleChoiceExercises()) {
                exerciseTypes.add(ExerciseTypes.SELECT_FLASHCARD);
            }

            if (options.getIncludeWritingExercises()) {
                exerciseTypes.add(ExerciseTypes.WRITE_FLASHCARD);
            }
        } else if (content instanceof PronunciationVertex) {
            if (options.getIncludeMultipleChoiceExercises() && options.getIncludeListeningExercises()) {
                exerciseTypes.add(ExerciseTypes.LISTEN_AND_SELECT);
            }

            if (options.getIncludeWritingExercises() && options.getIncludeListeningExercises()) {
                exerciseTypes.add(ExerciseTypes.LISTEN_AND_WRITE);
            }
        } else if (content instanceof TextContentVertex) {
            if (options.getIncludePronunciationExercises()) {
                exerciseTypes.add(ExerciseTypes.PRONOUNCE_FLASHCARD);
            }
        }

        return exerciseTypes;
    }

    private boolean hasSeenViewExerciseForContent(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex,
            AbstractVertex content,
            String contentId) {
        if (contentId != null && stateVertex != null && stateVertex.hasSeenViewExerciseForContent(contentId)) {
            return true;
        }

        return hasExerciseTypeForContentInSession(
                traversalSource,
                sessionVertex,
                content,
                ExerciseTypes.VIEW_FLASHCARD);
    }

    private boolean hasExerciseTypeForContentInSession(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            AbstractVertex content,
            String exerciseTypeId) {
        if (traversalSource == null || sessionVertex == null || content == null || exerciseTypeId == null) {
            return false;
        }

        var contentVertexId = content.getUnderlyingVertex().id();
        return traversalSource.V(sessionVertex.getUnderlyingVertex())
                .out(ExerciseSessionVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseVertex.LABEL)
                .where(__.out(ExerciseVertex.EDGE_CONTENT).hasId(contentVertexId))
                .where(__.out(ExerciseVertex.EDGE_TYPE)
                        .hasLabel(ExerciseTypeVertex.LABEL)
                        .has(ExerciseTypeVertex.PROPERTY_ID, exerciseTypeId))
                .hasNext();
    }

    private String getContentId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        }

        if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        }

        if (vertex instanceof FlashcardVertex flashcardVertex) {
            return flashcardVertex.getId();
        }

        return null;
    }

    public ExerciseVertex createMultiStageTapPairsExercise(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex) {
        var context = ExerciseCreationContext.createForSession(
                sessionVertex,
                ExerciseTypes.MULTI_STAGE_TAP_PAIRS);
        setContentStreamFromFlashcardDeck(context, sessionVertex);
        return multiStageTapPairsExerciseCreationManager.createExercise(traversalSource, context);
    }

    private void setContentStreamFromFlashcardDeck(
            ExerciseCreationContext context,
            ExerciseSessionVertex sessionVertex) {
        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck != null) {
            context.setContentStream(deckContentCrawler.crawl(flashcardDeck));
        }
    }

    private ExerciseVertex tryCreateReviewExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        String flashcardSide = null;
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null) {
            // Determine which side of the flashcard matches the target language
            if (flashcardVertex.getLeftContent() instanceof TextContentVertex leftTextContent &&
                leftTextContent.getLanguage().getId().equals(targetLanguage.getId())) {
                flashcardSide = "front";
            }
            else if (flashcardVertex.getRightContent() instanceof TextContentVertex rightTextContent &&
                     rightTextContent.getLanguage().getId().equals(targetLanguage.getId())) {
                flashcardSide = "back";
            }
        }

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                flashcardSide,
                ExerciseTypes.REVIEW_FLASHCARD);
        
        return reviewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateViewExercise(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                null,
                ExerciseTypes.VIEW_FLASHCARD);

        return viewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateSelectExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.SELECT_FLASHCARD);
        setContentStreamFromFlashcardDeck(context, sessionVertex);
        
        return selectFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateListenAndSelectExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex) {
        // Skip if pronunciation language doesn't match session target language
        var currentLanguageId = pronunciationVertex.getAudioContent().getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }
        
        var context = ExerciseCreationContext.createForPronunciation(
                sessionVertex,
                pronunciationVertex,
                ExerciseTypes.LISTEN_AND_SELECT);
        setContentStreamFromFlashcardDeck(context, sessionVertex);
        
        return listenAndSelectExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateWriteExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.WRITE_FLASHCARD);
        
        return writeFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateListenAndWriteExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex) {
        // Skip if pronunciation language doesn't match session target language
        var currentLanguageId = pronunciationVertex.getAudioContent().getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }
        
        var context = ExerciseCreationContext.createForPronunciation(
                sessionVertex,
                pronunciationVertex,
                ExerciseTypes.LISTEN_AND_WRITE);
        
        return listenAndWriteExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreatePronounceExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            TextContentVertex textContentVertex) {
        // Skip if text language doesn't match session target language
        var currentLanguageId = textContentVertex.getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }

        var context = ExerciseCreationContext.createForText(
                sessionVertex,
                textContentVertex,
                ExerciseTypes.PRONOUNCE_FLASHCARD);
        
        return pronounceFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }
}

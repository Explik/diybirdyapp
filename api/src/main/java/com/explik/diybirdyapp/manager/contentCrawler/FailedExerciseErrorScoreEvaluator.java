package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseFeedbackVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionStateVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates and caches hard-content error scores for learn-session crawling.
 *
 * Scores are recalculated from scratch on every call:
 * - previous cached values are removed
 * - exercise-level scores are recomputed from answer feedback
 * - exercise scores are propagated over the content graph
 * - propagated scores are cached on deck-relevant content vertices
 */
@Component
public class FailedExerciseErrorScoreEvaluator {

    public static final String PROPERTY_EXERCISE_ERROR_SCORE = "orbWeaverExerciseErrorScore";
    public static final String PROPERTY_CONTENT_ERROR_SCORE = "orbWeaverContentErrorScore";

    private static final String FEEDBACK_STATUS_INCORRECT = "incorrect";
    private static final String FEEDBACK_STATUS_CORRECT = "correct";
    private static final String FEEDBACK_TYPE_I_WAS_CORRECT = "i-was-correct";
        private static final String FEEDBACK_PROPERTY_TYPE = "type";
        private static final String FEEDBACK_PROPERTY_STATUS = "status";

    private static final double SCORE_INCORRECT = 3.0d;
    private static final double SCORE_CORRECT = -3.0d;
    private static final int MAX_PROPAGATION_STEPS = 3;

    private static final String[] PROPAGATION_EDGE_LABELS = new String[] {
            ExerciseVertex.EDGE_CONTENT,
            ExerciseVertex.EDGE_IS_BASED_ON,
            ExerciseVertex.EDGE_OPTION,
            ExerciseVertex.EDGE_CORRECT_OPTION,
            FlashcardVertex.EDGE_LEFT_CONTENT,
            FlashcardVertex.EDGE_RIGHT_CONTENT,
            TextContentVertex.EDGE_PRONUNCIATION,
            PronunciationVertex.EDGE_TEXT_CONTENT
    };

    /**
     * Recalculates and caches error scores for a session/deck combination.
     *
     * This method intentionally wipes old cached values before recomputing.
     */
    public void evaluate(FlashcardDeckVertex flashcardDeck, ExerciseSessionStateVertex sessionState) {
        if (flashcardDeck == null || sessionState == null) {
            return;
        }

        GraphTraversalSource traversalSource = sessionState.getUnderlyingSource();
        Vertex sessionStateVertex = sessionState.getUnderlyingVertex();
        Vertex flashcardDeckVertex = flashcardDeck.getUnderlyingVertex();

        wipePreviousScores(traversalSource, sessionStateVertex, flashcardDeckVertex);
        cacheExerciseScores(traversalSource, sessionStateVertex);

        boolean hasCalculatedExerciseScores = traversalSource.V(sessionStateVertex)
                .in(ExerciseSessionVertex.EDGE_STATE)
                .hasLabel(ExerciseSessionVertex.LABEL)
                .out(ExerciseSessionVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseVertex.LABEL)
                .has(PROPERTY_EXERCISE_ERROR_SCORE)
                .hasNext();

        if (!hasCalculatedExerciseScores) {
            return;
        }

        cachePropagatedContentScores(traversalSource, sessionStateVertex, flashcardDeckVertex);
    }

    private void wipePreviousScores(
            GraphTraversalSource traversalSource,
            Vertex sessionStateVertex,
            Vertex flashcardDeckVertex) {

        traversalSource.V(sessionStateVertex)
                .in(ExerciseSessionVertex.EDGE_STATE)
                .hasLabel(ExerciseSessionVertex.LABEL)
                .out(ExerciseSessionVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseVertex.LABEL)
                .properties(PROPERTY_EXERCISE_ERROR_SCORE)
                .drop()
                .iterate();

        traversalSource.V(flashcardDeckVertex)
                .out(FlashcardDeckVertex.EDGE_FLASHCARD)
                .as("flashcard")
                .union(
                        __.identity(),
                        __.out(FlashcardVertex.EDGE_LEFT_CONTENT),
                        __.out(FlashcardVertex.EDGE_RIGHT_CONTENT),
                        __.out(FlashcardVertex.EDGE_LEFT_CONTENT).out(TextContentVertex.EDGE_PRONUNCIATION),
                        __.out(FlashcardVertex.EDGE_RIGHT_CONTENT).out(TextContentVertex.EDGE_PRONUNCIATION))
                .dedup()
                .properties(PROPERTY_CONTENT_ERROR_SCORE)
                .drop()
                .iterate();
    }

    private void cacheExerciseScores(GraphTraversalSource traversalSource, Vertex sessionStateVertex) {
        List<Map<String, Object>> calculatedScores = traversalSource.V(sessionStateVertex)
                .in(ExerciseSessionVertex.EDGE_STATE)
                .hasLabel(ExerciseSessionVertex.LABEL)
                .out(ExerciseSessionVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseVertex.LABEL)
                .where(hasScorableFeedback())
                .project("exercise", "score")
                .by(__.identity())
                .by(answerScoreSumTraversal())
                .toList();

        for (Map<String, Object> row : calculatedScores) {
            Vertex exerciseVertex = (Vertex) row.get("exercise");
            Number scoreValue = (Number) row.get("score");
            if (exerciseVertex == null || scoreValue == null) {
                continue;
            }

            traversalSource.V(exerciseVertex)
                    .property(PROPERTY_EXERCISE_ERROR_SCORE, scoreValue.doubleValue())
                    .iterate();
        }
    }

    private void cachePropagatedContentScores(
            GraphTraversalSource traversalSource,
            Vertex sessionStateVertex,
            Vertex flashcardDeckVertex) {

        Set<Object> deckScopedVertexIds = new HashSet<>(traversalSource.V(flashcardDeckVertex)
                .out(FlashcardDeckVertex.EDGE_FLASHCARD)
                .as("flashcard")
                .union(
                        __.identity(),
                        __.out(FlashcardVertex.EDGE_LEFT_CONTENT),
                        __.out(FlashcardVertex.EDGE_RIGHT_CONTENT),
                        __.out(FlashcardVertex.EDGE_LEFT_CONTENT).out(TextContentVertex.EDGE_PRONUNCIATION),
                        __.out(FlashcardVertex.EDGE_RIGHT_CONTENT).out(TextContentVertex.EDGE_PRONUNCIATION))
                .dedup()
                .id()
                .toList());

        if (deckScopedVertexIds.isEmpty()) {
            return;
        }

        Map<Object, Object> propagatedScores = traversalSource.withSack(0.0d)
                .V(sessionStateVertex)
                .in(ExerciseSessionVertex.EDGE_STATE)
                .hasLabel(ExerciseSessionVertex.LABEL)
                .out(ExerciseSessionVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseVertex.LABEL)
                .has(PROPERTY_EXERCISE_ERROR_SCORE)
                .sack(Operator.assign).by(__.values(PROPERTY_EXERCISE_ERROR_SCORE))
                .emit()
                .repeat(
                        __.bothE(PROPAGATION_EDGE_LABELS)
                                .otherV()
                                .simplePath()
                                .sack(Operator.sum).by(__.constant(-1.0d)))
                .times(MAX_PROPAGATION_STEPS)
                .hasLabel(P.within(FlashcardVertex.LABEL, TextContentVertex.LABEL, PronunciationVertex.LABEL))
                .group()
                .by(T.id)
                .by(__.sack().sum())
                .tryNext()
                .orElseGet(HashMap::new);

                for (Map.Entry<Object, Object> scoreEntry : propagatedScores.entrySet()) {
            Object vertexId = scoreEntry.getKey();
                        Object rawScoreValue = scoreEntry.getValue();
                        Number scoreValue = rawScoreValue instanceof Number ? (Number) rawScoreValue : null;

            if (vertexId == null || scoreValue == null || !deckScopedVertexIds.contains(vertexId)) {
                continue;
            }

            traversalSource.V(vertexId)
                    .property(PROPERTY_CONTENT_ERROR_SCORE, scoreValue.doubleValue())
                    .iterate();
        }
    }

    private GraphTraversal<?, ?> hasScorableFeedback() {
        return __.in(ExerciseAnswerVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseAnswerVertex.LABEL)
                .in(ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER)
                .hasLabel(ExerciseFeedbackVertex.LABEL)
                .or(
                        __.has(FEEDBACK_PROPERTY_TYPE, FEEDBACK_TYPE_I_WAS_CORRECT),
                        __.has(FEEDBACK_PROPERTY_STATUS, FEEDBACK_STATUS_INCORRECT),
                        __.has(FEEDBACK_PROPERTY_STATUS, FEEDBACK_STATUS_CORRECT));
    }

    private GraphTraversal<?, ?> answerScoreSumTraversal() {
        return __.in(ExerciseAnswerVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseAnswerVertex.LABEL)
                .map(
                        __.coalesce(
                                __.in(ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER)
                                        .hasLabel(ExerciseFeedbackVertex.LABEL)
                                        .has(FEEDBACK_PROPERTY_TYPE, FEEDBACK_TYPE_I_WAS_CORRECT)
                                        .limit(1)
                                        .constant(SCORE_CORRECT),
                                __.in(ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER)
                                        .hasLabel(ExerciseFeedbackVertex.LABEL)
                                        .has(FEEDBACK_PROPERTY_STATUS, FEEDBACK_STATUS_INCORRECT)
                                        .limit(1)
                                        .constant(SCORE_INCORRECT),
                                __.in(ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER)
                                        .hasLabel(ExerciseFeedbackVertex.LABEL)
                                        .has(FEEDBACK_PROPERTY_STATUS, FEEDBACK_STATUS_CORRECT)
                                        .limit(1)
                                        .constant(SCORE_CORRECT),
                                __.constant(0.0d)))
                .sum();
    }
}
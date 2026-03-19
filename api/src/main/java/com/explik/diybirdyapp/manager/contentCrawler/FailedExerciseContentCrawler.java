package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Hard-content crawler that retrieves deck content with elevated propagated error scores.
 *
 * Scores are expected to be precomputed and cached by {@link FailedExerciseErrorScoreEvaluator}.
 * If score calculation has not been performed yet for the session, this crawler returns no content.
 */
@Component
public class FailedExerciseContentCrawler implements ContentCrawler<FlashcardDeckSessionParams> {

    private static final double MIN_ERROR_SCORE = 0.0d;
    
    /**
     * Returns hard content for the current session/deck based on cached propagated error scores.
     */
    @Override
    public Stream<AbstractVertex> crawl(FlashcardDeckSessionParams params) {
        if (params == null || params.flashcardDeck() == null || params.sessionState() == null) {
            return Stream.empty();
        }

        return collectNextFlashcardContent(params.flashcardDeck(), params.sessionState());
    }

    private Stream<AbstractVertex> collectNextFlashcardContent(
            FlashcardDeckVertex flashcardDeck,
            ExerciseSessionStateVertex sessionState) {
        GraphTraversalSource traversalSource = sessionState.getUnderlyingSource();
        Vertex sessionStateVertex = sessionState.getUnderlyingVertex();
        Vertex flashcardDeckVertex = flashcardDeck.getUnderlyingVertex();

        // Ignore content retrieval until exercise scores have been precomputed.
        boolean hasCalculatedExerciseScores = traversalSource.V(sessionStateVertex)
                .in(ExerciseSessionVertex.EDGE_STATE)
                .hasLabel(ExerciseSessionVertex.LABEL)
                .out(ExerciseSessionVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseVertex.LABEL)
                .has(FailedExerciseErrorScoreEvaluator.PROPERTY_EXERCISE_ERROR_SCORE)
                .hasNext();

        if (!hasCalculatedExerciseScores) {
            return Stream.empty();
        }

        Set<Object> activeVertexIds = new HashSet<>(traversalSource.V(sessionStateVertex)
                .out(ExerciseSessionStateVertex.EDGE_ACTIVE_CONTENT)
                .id()
                .toList());

        GraphTraversal<Vertex, Vertex> hardContentTraversal = traversalSource.V(flashcardDeckVertex)
                .out(FlashcardDeckVertex.EDGE_FLASHCARD)
                .as("flashcard")
                .union(
                        __.identity(),
                        __.out(FlashcardVertex.EDGE_LEFT_CONTENT),
                        __.out(FlashcardVertex.EDGE_RIGHT_CONTENT),
                        __.out(FlashcardVertex.EDGE_LEFT_CONTENT).out(TextContentVertex.EDGE_PRONUNCIATION),
                        __.out(FlashcardVertex.EDGE_RIGHT_CONTENT).out(TextContentVertex.EDGE_PRONUNCIATION))
                .dedup()
                .has(FailedExerciseErrorScoreEvaluator.PROPERTY_CONTENT_ERROR_SCORE, P.gt(MIN_ERROR_SCORE));

        if (!activeVertexIds.isEmpty()) {
            hardContentTraversal = hardContentTraversal.not(__.hasId(P.within(activeVertexIds)));
        }

        return hardContentTraversal
                .order().by(Order.shuffle)
                .toStream()
                .map(vertex -> mapToModel(traversalSource, vertex))
                .filter(java.util.Objects::nonNull);
    }

    private AbstractVertex mapToModel(GraphTraversalSource traversalSource, Vertex vertex) {
        if (vertex == null) {
            return null;
        }

        String label = vertex.label();
        if (PronunciationVertex.LABEL.equals(label)) {
            return new PronunciationVertex(traversalSource, vertex);
        }

        if (FlashcardVertex.LABEL.equals(label)
                || TextContentVertex.LABEL.equals(label)
                || AudioContentVertex.LABEL.equals(label)
                || ImageContentVertex.LABEL.equals(label)
                || VideoContentVertex.LABEL.equals(label)) {
            return VertexHelper.createContent(traversalSource, vertex);
        }

        return null;
    }
}

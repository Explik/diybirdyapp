package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * Insufficiently Exercised Content Crawler - Retrieves lightly-practiced content.
 *
 * Updated flow for "lightly-practiced" selection:
 * 1. Select practiced root content (flashcards) from session state.
 * 2. Keep roots with insufficient associated exercise practice.
 * 3. Select associated content for kept roots (text + pronunciations).
 * 4. Return as grouped output: one root, then its associated content.
 */
@Component
public class InsufficientlyExercisedContentCrawler implements
    ContentCrawler<FlashcardDeckSessionParams>,
    GroupedContentCrawler<FlashcardDeckSessionParams> {

    private static final int REQUIRED_PRACTICES_TEXT = 1;
    private static final int REQUIRED_PRACTICES_PRONUNCIATION = 2;

    /**
     * Collects lightly-practiced content for the next batch.
     */
    @Override
    public Stream<AbstractVertex> crawl(FlashcardDeckSessionParams params) {
        return crawlGroups(params).flatMap(List::stream);
    }

    @Override
    public Stream<List<AbstractVertex>> crawlGroups(FlashcardDeckSessionParams params) {
        if (params == null || params.flashcardDeck() == null || params.sessionState() == null) {
            return Stream.empty();
        }

        return collectNextFlashcardContentGroups(params.flashcardDeck(), params.sessionState());
    }

    private Stream<List<AbstractVertex>> collectNextFlashcardContentGroups(
            FlashcardDeckVertex flashcardDeck,
            ExerciseSessionStateVertex sessionState) {

        GraphTraversalSource traversalSource = sessionState.getUnderlyingSource();
        Vertex sessionStateVertex = sessionState.getUnderlyingVertex();
        Vertex flashcardDeckVertex = flashcardDeck.getUnderlyingVertex();

        Set<String> activeVertexIds = collectActiveContentIds(traversalSource, sessionStateVertex);
        Map<String, Integer> exerciseCounts = collectExerciseCountsByContentId(traversalSource, sessionStateVertex);
        String targetLanguageId = resolveTargetLanguageId(traversalSource, sessionStateVertex);

        List<Vertex> practicedFlashcards = traversalSource.V(flashcardDeckVertex)
                .outE(FlashcardDeckVertex.EDGE_FLASHCARD)
                .order().by(FlashcardDeckVertex.EDGE_FLASHCARD_PROPERTY_ORDER)
                .inV()
                .hasLabel(FlashcardVertex.LABEL)
                .where(__.in(ExerciseSessionStateVertex.EDGE_PRACTICED_CONTENT).is(sessionStateVertex))
                .toList();

        if (practicedFlashcards.isEmpty()) {
            return Stream.empty();
        }

        // Shuffle roots to avoid repeatedly favoring low-order deck flashcards
        // when callers apply a limit to the crawler output.
        Collections.shuffle(practicedFlashcards);

        List<List<AbstractVertex>> selectedContentGroups = new ArrayList<>();
        Set<String> emittedVertexIds = new HashSet<>();

        for (Vertex flashcardVertex : practicedFlashcards) {
            List<Vertex> associatedVertices = collectAssociatedVertices(
                    traversalSource,
                    flashcardVertex,
                    targetLanguageId);

            boolean flashcardNeedsPractice = needsMorePractice(flashcardVertex, exerciseCounts);
            List<Vertex> associatedNeedingPractice = associatedVertices.stream()
                    .filter(vertex -> needsMorePractice(vertex, exerciseCounts))
                    .toList();

            if (!flashcardNeedsPractice && associatedNeedingPractice.isEmpty()) {
                continue;
            }

            List<AbstractVertex> associatedGroupContent = new ArrayList<>();
            for (Vertex associatedVertex : associatedNeedingPractice) {
                addVertexIfEligible(
                        associatedGroupContent,
                        traversalSource,
                        associatedVertex,
                        activeVertexIds,
                        emittedVertexIds);
            }

            if (!flashcardNeedsPractice && associatedGroupContent.isEmpty()) {
                continue;
            }

            List<AbstractVertex> group = new ArrayList<>();

            boolean includeActiveRootForContext = !associatedGroupContent.isEmpty();
            boolean addedRoot = addRootFlashcardToGroup(
                    group,
                    traversalSource,
                    flashcardVertex,
                    activeVertexIds,
                    includeActiveRootForContext,
                    emittedVertexIds);

            if (!addedRoot) {
                // Keep associated content tied to a flashcard context group.
                continue;
            }

            group.addAll(associatedGroupContent);

            if (!group.isEmpty()) {
                selectedContentGroups.add(group);
            }
        }

        return selectedContentGroups.stream();
    }

    private boolean addRootFlashcardToGroup(
            List<AbstractVertex> group,
            GraphTraversalSource traversalSource,
            Vertex flashcardVertex,
            Set<String> activeVertexIds,
            boolean includeIfActive,
            Set<String> emittedVertexIds) {
        String flashcardId = getVertexId(flashcardVertex);
        if (flashcardId == null || (!includeIfActive && activeVertexIds.contains(flashcardId)) || !emittedVertexIds.add(flashcardId)) {
            return false;
        }

        AbstractVertex mappedVertex = mapToVertexModel(traversalSource, flashcardVertex);
        if (mappedVertex != null) {
            group.add(mappedVertex);
            return true;
        }

        return false;
    }

    private Set<String> collectActiveContentIds(GraphTraversalSource traversalSource, Vertex sessionStateVertex) {
        Set<String> ids = new HashSet<>();
        var directActiveIds = traversalSource.V(sessionStateVertex)
                .out(ExerciseSessionStateVertex.EDGE_ACTIVE_CONTENT)
                .has(ContentVertex.PROPERTY_ID)
                .values(ContentVertex.PROPERTY_ID)
                .toList();

        var groupedActiveIds = traversalSource.V(sessionStateVertex)
                .out(ExerciseSessionStateVertex.EDGE_ITEM)
                .hasLabel(ExerciseSessionStateVertex.LABEL)
                .out(ExerciseSessionStateVertex.EDGE_ACTIVE_CONTENT)
                .has(ContentVertex.PROPERTY_ID)
                .values(ContentVertex.PROPERTY_ID)
                .toList();

        for (Object idValue : directActiveIds) {
            if (idValue != null) {
                ids.add(idValue.toString());
            }
        }

        for (Object idValue : groupedActiveIds) {
            if (idValue != null) {
                ids.add(idValue.toString());
            }
        }
        return ids;
    }

    private Map<String, Integer> collectExerciseCountsByContentId(
            GraphTraversalSource traversalSource,
            Vertex sessionStateVertex) {

        Map<Object, Long> rawCounts = traversalSource.V(sessionStateVertex)
                .in(ExerciseSessionVertex.EDGE_STATE)
                .hasLabel(ExerciseSessionVertex.LABEL)
                .out(ExerciseSessionVertex.EDGE_EXERCISE)
                .hasLabel(ExerciseVertex.LABEL)
                .union(
                        __.out(ExerciseVertex.EDGE_CONTENT),
                        __.out(ExerciseVertex.EDGE_IS_BASED_ON),
                        __.out(ExerciseVertex.EDGE_OPTION),
                        __.out(ExerciseVertex.EDGE_CORRECT_OPTION))
                .has(ContentVertex.PROPERTY_ID)
                .values(ContentVertex.PROPERTY_ID)
                .groupCount()
                .tryNext()
                .orElseGet(HashMap::new);

        Map<String, Integer> counts = new HashMap<>();
        rawCounts.forEach((contentId, count) -> {
            if (contentId != null && count != null) {
                counts.put(contentId.toString(), count.intValue());
            }
        });

        return counts;
    }

    private String resolveTargetLanguageId(GraphTraversalSource traversalSource, Vertex sessionStateVertex) {
        return traversalSource.V(sessionStateVertex)
                .in(ExerciseSessionVertex.EDGE_STATE)
                .hasLabel(ExerciseSessionVertex.LABEL)
                .out(ExerciseSessionVertex.EDGE_OPTIONS)
                .hasLabel(ExerciseSessionOptionsVertex.LABEL)
                .out(ExerciseSessionOptionsVertex.EDGE_TARGET_LANGUAGE)
                .hasLabel(LanguageVertex.LABEL)
                .values(LanguageVertex.PROPERTY_ID)
                .tryNext()
                .map(Object::toString)
                .orElse(null);
    }

    private List<Vertex> collectAssociatedVertices(
            GraphTraversalSource traversalSource,
            Vertex flashcardVertex,
            String targetLanguageId) {

        List<Vertex> associatedVertices = new ArrayList<>();
        addTextSideAndPronunciations(
                associatedVertices,
                traversalSource,
                flashcardVertex,
                FlashcardVertex.EDGE_LEFT_CONTENT,
                targetLanguageId);
        addTextSideAndPronunciations(
                associatedVertices,
                traversalSource,
                flashcardVertex,
                FlashcardVertex.EDGE_RIGHT_CONTENT,
                targetLanguageId);

        return associatedVertices;
    }

    private void addTextSideAndPronunciations(
            List<Vertex> associatedVertices,
            GraphTraversalSource traversalSource,
            Vertex flashcardVertex,
            String sideEdge,
            String targetLanguageId) {

        Vertex textContentVertex = traversalSource.V(flashcardVertex)
                .out(sideEdge)
                .hasLabel(TextContentVertex.LABEL)
                .tryNext()
                .orElse(null);

        if (textContentVertex == null) {
            return;
        }

        associatedVertices.add(textContentVertex);

        var pronunciationTraversal = traversalSource.V(textContentVertex)
                .out(TextContentVertex.EDGE_PRONUNCIATION)
                .hasLabel(PronunciationVertex.LABEL);

        if (targetLanguageId != null) {
            pronunciationTraversal = pronunciationTraversal.where(
                    __.out(PronunciationVertex.EDGE_TEXT_CONTENT)
                            .out(TextContentVertex.EDGE_LANGUAGE)
                            .has(LanguageVertex.PROPERTY_ID, targetLanguageId));
        }

        associatedVertices.addAll(pronunciationTraversal.toList());
    }

    private boolean needsMorePractice(Vertex vertex, Map<String, Integer> exerciseCounts) {
        String vertexId = getVertexId(vertex);
        if (vertexId == null) {
            return false;
        }

        int requiredPracticeCount = getRequiredPracticeCount(vertex.label());
        if (requiredPracticeCount <= 0) {
            return false;
        }

        int currentPracticeCount = exerciseCounts.getOrDefault(vertexId, 0);
        return currentPracticeCount < requiredPracticeCount;
    }

    private int getRequiredPracticeCount(String vertexLabel) {
        if (FlashcardVertex.LABEL.equals(vertexLabel)) {
            return ExerciseSessionStateVertex.MAX_EXERCISES_PER_CONTENT;
        }
        if (TextContentVertex.LABEL.equals(vertexLabel)) {
            return REQUIRED_PRACTICES_TEXT;
        }
        if (PronunciationVertex.LABEL.equals(vertexLabel)) {
            return REQUIRED_PRACTICES_PRONUNCIATION;
        }
        return 1;
    }

    private void addVertexIfEligible(
            List<AbstractVertex> selectedContent,
            GraphTraversalSource traversalSource,
            Vertex vertex,
            Set<String> activeVertexIds,
            Set<String> emittedVertexIds) {

        String vertexId = getVertexId(vertex);
        if (vertexId == null || activeVertexIds.contains(vertexId) || !emittedVertexIds.add(vertexId)) {
            return;
        }

        AbstractVertex mappedVertex = mapToVertexModel(traversalSource, vertex);
        if (mappedVertex != null) {
            selectedContent.add(mappedVertex);
        }
    }

    private AbstractVertex mapToVertexModel(GraphTraversalSource traversalSource, Vertex vertex) {
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

    /**
     * Reads the shared content ID property from a raw graph vertex.
     */
    private String getVertexId(Vertex vertex) {
        if (vertex == null || !vertex.property(ContentVertex.PROPERTY_ID).isPresent()) {
            return null;
        }

        Object idValue = vertex.property(ContentVertex.PROPERTY_ID).value();
        return idValue != null ? idValue.toString() : null;
    }
}

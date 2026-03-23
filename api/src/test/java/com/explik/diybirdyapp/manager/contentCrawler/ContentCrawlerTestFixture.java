package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseFeedbackVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionStateVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ContentCrawlerTestFixture {
    final GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    void setExerciseErrorScore(ExerciseVertex exercise, double score) {
        traversalSource.V(exercise.getUnderlyingVertex())
                .property(FailedExerciseErrorScoreEvaluator.PROPERTY_EXERCISE_ERROR_SCORE, score)
                .iterate();
    }

    void setContentErrorScore(AbstractVertex contentVertex, double score) {
        traversalSource.V(contentVertex.getUnderlyingVertex())
                .property(FailedExerciseErrorScoreEvaluator.PROPERTY_CONTENT_ERROR_SCORE, score)
                .iterate();
    }

    boolean hasProperty(AbstractVertex vertex, String propertyName) {
        return traversalSource.V(vertex.getUnderlyingVertex())
                .properties(propertyName)
                .hasNext();
    }

    Double getNumericProperty(AbstractVertex vertex, String propertyName) {
        return traversalSource.V(vertex.getUnderlyingVertex())
                .values(propertyName)
                .tryNext()
                .map(value -> ((Number) value).doubleValue())
                .orElse(null);
    }

    LanguageVertex createLanguage(String id) {
        var language = LanguageVertex.create(traversalSource);
        language.setId(id);
        return language;
    }

    TextContentVertex createTextContent(String id, LanguageVertex language) {
        var textContent = TextContentVertex.create(traversalSource);
        textContent.setId(id);
        if (language != null) {
            textContent.setLanguage(language);
        }
        return textContent;
    }

    PronunciationVertex createPronunciation(String id, TextContentVertex ownerTextContent, TextContentVertex pronunciationTextContent) {
        var pronunciation = PronunciationVertex.create(traversalSource);
        pronunciation.setId(id);

        if (pronunciationTextContent != null) {
            pronunciation.setTextContent(pronunciationTextContent);
        }

        traversalSource.V(ownerTextContent.getUnderlyingVertex())
                .addE(TextContentVertex.EDGE_PRONUNCIATION)
                .to(pronunciation.getUnderlyingVertex())
                .next();

        return pronunciation;
    }

    FlashcardVertex createFlashcard(String id, ContentVertex leftContent, ContentVertex rightContent) {
        var flashcard = FlashcardVertex.create(traversalSource);
        flashcard.setId(id);
        if (leftContent != null) {
            flashcard.setLeftContent(leftContent);
        }
        if (rightContent != null) {
            flashcard.setRightContent(rightContent);
        }
        return flashcard;
    }

    FlashcardDeckVertex createDeck(String id, FlashcardVertex... flashcards) {
        var deck = FlashcardDeckVertex.create(traversalSource);
        deck.setId(id);
        Arrays.stream(flashcards).forEach(deck::addFlashcard);
        return deck;
    }

    ExerciseSessionStateVertex createSessionState(String type) {
        var state = ExerciseSessionStateVertex.create(traversalSource);
        state.setType(type);
        return state;
    }

    ExerciseSessionVertex createSessionWithOptions(String id, ExerciseSessionStateVertex state, boolean shuffleFlashcards,
                                                   LanguageVertex targetLanguage) {
        var session = ExerciseSessionVertex.create(traversalSource);
        session.setId(id);
        session.addState(state);

        var options = ExerciseSessionOptionsVertex.create(traversalSource);
        options.setId("options-" + id);
        options.setShuffleFlashcards(shuffleFlashcards);
        if (targetLanguage != null) {
            options.setTargetLanguage(targetLanguage);
        }
        session.setOptions(options);

        return session;
    }

    ExerciseSessionVertex createSessionWithoutOptions(String id, ExerciseSessionStateVertex state) {
        var session = ExerciseSessionVertex.create(traversalSource);
        session.setId(id);
        session.addState(state);
        return session;
    }

    ExerciseVertex createExercise(String id, ExerciseSessionVertex session, ContentVertex content) {
        var exercise = ExerciseVertex.create(traversalSource);
        exercise.setId(id);
        exercise.setContent(content);
        session.addExercise(exercise);
        return exercise;
    }

    ExerciseAnswerVertex createAnswer(String id, ExerciseVertex exercise) {
        var answer = ExerciseAnswerVertex.create(traversalSource);
        answer.setId(id);
        answer.setExercise(exercise);
        return answer;
    }

    ExerciseFeedbackVertex createFeedback(String id, ExerciseAnswerVertex answer, String status, String type) {
        var feedback = ExerciseFeedbackVertex.create(traversalSource);
        feedback.setId(id);
        if (status != null) {
            feedback.setStatus(status);
        }
        if (type != null) {
            feedback.setType(type);
        }
        feedback.setAnswer(answer);
        return feedback;
    }

    FlashcardDeckSessionParams params(FlashcardDeckVertex flashcardDeck, ExerciseSessionStateVertex sessionState) {
        return new FlashcardDeckSessionParams(flashcardDeck, sessionState);
    }

    String idOf(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        }
        if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        }
        return null;
    }

    List<String> toIdList(Stream<AbstractVertex> contentStream) {
        return contentStream
                .map(this::idOf)
                .filter(Objects::nonNull)
                .toList();
    }

    Set<String> toIdSet(Stream<AbstractVertex> contentStream) {
        return toIdList(contentStream).stream().collect(Collectors.toSet());
    }
}

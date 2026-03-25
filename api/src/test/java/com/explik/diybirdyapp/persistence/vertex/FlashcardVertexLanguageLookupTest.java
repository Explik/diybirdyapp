package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlashcardVertexLanguageLookupTest {

    @Test
    void findSideByLanguageId_whenLeftLanguageMatches_returnsFront() {
        var traversalSource = TinkerGraph.open().traversal();
        var flashcard = createFlashcard(traversalSource, "lang-left", "lang-right");

        var side = FlashcardVertex.findSideByLanguageId(flashcard, "lang-left");

        assertEquals("front", side);
    }

    @Test
    void findSideByLanguageId_whenRightLanguageMatches_returnsBack() {
        var traversalSource = TinkerGraph.open().traversal();
        var flashcard = createFlashcard(traversalSource, "lang-left", "lang-right");

        var side = FlashcardVertex.findSideByLanguageId(flashcard, "lang-right");

        assertEquals("back", side);
    }

    @Test
    void findSideByLanguageId_whenLanguageDoesNotMatch_returnsFallback() {
        var traversalSource = TinkerGraph.open().traversal();
        var flashcard = createFlashcard(traversalSource, "lang-left", "lang-right");

        var side = FlashcardVertex.findSideByLanguageId(flashcard, "lang-unknown", "back");

        assertEquals("back", side);
    }

    @Test
    void findSideByLanguageId_whenLanguageIsNull_returnsFrontByDefault() {
        var traversalSource = TinkerGraph.open().traversal();
        var flashcard = createFlashcard(traversalSource, "lang-left", "lang-right");

        var side = FlashcardVertex.findSideByLanguageId(flashcard, null);

        assertEquals("front", side);
    }

    @Test
    void findPromptSideForAnswerLanguageId_whenAnswerIsLeft_returnsBack() {
        var traversalSource = TinkerGraph.open().traversal();
        var flashcard = createFlashcard(traversalSource, "lang-left", "lang-right");

        var side = FlashcardVertex.findPromptSideForAnswerLanguageId(flashcard, "lang-left");

        assertEquals("back", side);
    }

    @Test
    void findPromptSideForAnswerLanguageId_whenAnswerIsRight_returnsFront() {
        var traversalSource = TinkerGraph.open().traversal();
        var flashcard = createFlashcard(traversalSource, "lang-left", "lang-right");

        var side = FlashcardVertex.findPromptSideForAnswerLanguageId(flashcard, "lang-right");

        assertEquals("front", side);
    }

    @Test
    void findPromptSideForAnswerLanguageId_whenAnswerLanguageUnknown_returnsFront() {
        var traversalSource = TinkerGraph.open().traversal();
        var flashcard = createFlashcard(traversalSource, "lang-left", "lang-right");

        var side = FlashcardVertex.findPromptSideForAnswerLanguageId(flashcard, "lang-unknown");

        assertEquals("front", side);
    }

    @Test
    void findPromptSideForAnswerLanguageId_whenAnswerLanguageNull_returnsFront() {
        var traversalSource = TinkerGraph.open().traversal();
        var flashcard = createFlashcard(traversalSource, "lang-left", "lang-right");

        var side = FlashcardVertex.findPromptSideForAnswerLanguageId(flashcard, null);

        assertEquals("front", side);
    }

    private FlashcardVertex createFlashcard(
            GraphTraversalSource traversalSource,
            String leftLanguageId,
            String rightLanguageId) {
        var leftLanguage = LanguageVertex.create(traversalSource);
        leftLanguage.setId(leftLanguageId);

        var rightLanguage = LanguageVertex.create(traversalSource);
        rightLanguage.setId(rightLanguageId);

        var leftContent = TextContentVertex.create(traversalSource);
        leftContent.setLanguage(leftLanguage);

        var rightContent = TextContentVertex.create(traversalSource);
        rightContent.setLanguage(rightLanguage);

        var flashcard = FlashcardVertex.create(traversalSource);
        flashcard.setLeftContent(leftContent);
        flashcard.setRightContent(rightContent);

        return flashcard;
    }
}

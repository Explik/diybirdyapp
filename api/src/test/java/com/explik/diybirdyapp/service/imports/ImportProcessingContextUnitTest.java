package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportAttachmentModel;
import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.model.imports.ImportManifestModel;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportProcessingContextUnitTest {
    @Test
    void givenContentRefResolvedTwice_whenResolveContentRef_thenUsesCachedVertex() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");

        var createCount = new AtomicInteger(0);
        var contentHandler = new ImportContentHandler() {
            @Override
            public String getSupportedContentType() {
                return "test-content";
            }

            @Override
            public ContentVertex createContent(ImportContentDto content, ImportProcessingContext context) {
                createCount.incrementAndGet();
                var vertex = TextContentVertex.create(traversalSource);
                vertex.setId("generated-content");
                vertex.setLanguage(language);
                return vertex;
            }
        };

        var conceptHandler = new ImportConceptHandler() {
            @Override
            public String getSupportedConceptType() {
                return "test-concept";
            }

            @Override
            public void applyConcept(ImportConceptDto concept, ImportProcessingContext context) {
            }
        };

        var context = createContext(traversalSource, contentHandler, conceptHandler);

        var first = context.resolveContentRef("content-1");
        var second = context.resolveContentRef("content-1");

        assertSame(first, second);
        assertEquals(1, createCount.get());
    }

    @Test
    void givenAppliedConceptTwice_whenApplyConceptRef_thenRunsOnce() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

        var contentHandler = new ImportContentHandler() {
            @Override
            public String getSupportedContentType() {
                return "test-content";
            }

            @Override
            public ContentVertex createContent(ImportContentDto content, ImportProcessingContext context) {
                return TextContentVertex.create(traversalSource);
            }
        };

        var applyCount = new AtomicInteger(0);
        var conceptHandler = new ImportConceptHandler() {
            @Override
            public String getSupportedConceptType() {
                return "test-concept";
            }

            @Override
            public void applyConcept(ImportConceptDto concept, ImportProcessingContext context) {
                applyCount.incrementAndGet();
            }
        };

        var context = createContext(traversalSource, contentHandler, conceptHandler);

        context.applyConceptRef("concept-1");
        context.applyConceptRef("concept-1");

        assertEquals(1, applyCount.get());
    }

    @Test
    void givenUndeclaredAttachment_whenRequireDeclaredAttachment_thenThrows() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

        var context = createContext(
                traversalSource,
                contentModel -> TextContentVertex.create(traversalSource),
                conceptModel -> {});

        var exception = assertThrows(IllegalArgumentException.class,
                () -> context.requireDeclaredAttachment("media/missing.mp3"));

        assertEquals("Undeclared fileRef: media/missing.mp3", exception.getMessage());
    }

    @Test
    void givenAttachmentWithBackslashes_whenRequireDeclaredAttachment_thenNormalizesPath() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

        var context = createContext(
                traversalSource,
                contentModel -> TextContentVertex.create(traversalSource),
                conceptModel -> {});

        var attachment = new ImportAttachmentModel();
        attachment.setFileRef("media/file.mp3");
        context.getJob().getAttachments().put("media/file.mp3", attachment);

        var found = context.requireDeclaredAttachment("media\\file.mp3");

        assertSame(attachment, found);
    }

    @Test
    void givenKnownLanguage_whenFindLanguageById_thenReturnsLanguage() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");

        var context = createContext(
                traversalSource,
                contentModel -> TextContentVertex.create(traversalSource),
                conceptModel -> {});

        var found = context.findLanguageById("lang-1");

        assertEquals("lang-1", found.getId());
    }

    @Test
    void givenUnknownLanguage_whenFindLanguageById_thenThrows() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

        var context = createContext(
                traversalSource,
                contentModel -> TextContentVertex.create(traversalSource),
                conceptModel -> {});

        var exception = assertThrows(IllegalArgumentException.class,
                () -> context.findLanguageById("missing-lang"));

        assertEquals("Language not found: missing-lang", exception.getMessage());
    }

    @Test
    void givenAudioWithoutLanguage_thenHasAudioLanguageReturnsFalse() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var audio = AudioContentVertex.create(traversalSource);
        audio.setId("audio-1");

        var context = createContext(
                traversalSource,
                contentModel -> TextContentVertex.create(traversalSource),
                conceptModel -> {});

        assertFalse(context.hasAudioLanguage(audio));

        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");
        audio.setLanguage(language);

        assertTrue(context.hasAudioLanguage(audio));
    }

    @FunctionalInterface
    private interface ContentCreator {
        ContentVertex create(ImportContentDto contentModel);
    }

    @FunctionalInterface
    private interface ConceptApplier {
        void apply(ImportConceptDto conceptModel);
    }

    private ImportProcessingContext createContext(
            GraphTraversalSource traversalSource,
            ContentCreator contentCreator,
            ConceptApplier conceptApplier) {
        var contentHandler = new ImportContentHandler() {
            @Override
            public String getSupportedContentType() {
                return "test-content";
            }

            @Override
            public ContentVertex createContent(ImportContentDto content, ImportProcessingContext context) {
                return contentCreator.create(content);
            }
        };

        var conceptHandler = new ImportConceptHandler() {
            @Override
            public String getSupportedConceptType() {
                return "test-concept";
            }

            @Override
            public void applyConcept(ImportConceptDto concept, ImportProcessingContext context) {
                conceptApplier.apply(concept);
            }
        };

        return createContext(traversalSource, contentHandler, conceptHandler);
    }

    private ImportProcessingContext createContext(
            GraphTraversalSource traversalSource,
            ImportContentHandler contentHandler,
            ImportConceptHandler conceptHandler) {
        var manifest = new ImportManifestModel();

        var content = new ImportContentDto();
        content.setContentId("content-1");
        content.setContentType("test-content");
        content.setPayload(Map.of("value", "test"));
        manifest.setContents(List.of(content));

        var concept = new ImportConceptDto();
        concept.setConceptId("concept-1");
        concept.setConceptType("test-concept");
        concept.setPayload(Map.of("value", "test"));
        manifest.setConcepts(List.of(concept));

        var job = new ImportJobModel();
        job.setJobId("job-1");

        var deck = FlashcardDeckVertex.create(traversalSource);
        deck.setId("deck-1");

        return new ImportProcessingContext(
                job,
                manifest,
                traversalSource,
                new ImportContentHandlerRegistry(List.of(contentHandler)),
                new ImportConceptHandlerRegistry(List.of(conceptHandler)),
                deck);
    }
}


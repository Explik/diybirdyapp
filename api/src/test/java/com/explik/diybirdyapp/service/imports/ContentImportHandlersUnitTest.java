package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportAttachmentModel;
import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordSlotDto;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TranscriptionVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContentImportHandlersUnitTest {
    @Test
    void givenTextContent_whenCreate_thenCreatesTextVertexAndTracksId() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.findLanguageById("lang-1")).thenReturn(language);

        var content = new ImportContentDto();
        content.setPayload(Map.of("text", "hello", "languageId", "lang-1"));

        var handler = new TextImportContentHandler();
        var vertex = handler.createContent(content, context);

        assertEquals("hello", vertex.getValue());
        assertEquals("lang-1", vertex.getLanguage().getId());
        verify(context).trackCreatedContentId(vertex.getId());
    }

    @Test
    void givenAudioUploadContent_whenCreate_thenCreatesAudioVertexWithLanguage() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.findLanguageById("lang-1")).thenReturn(language);
        when(context.requireDeclaredAttachment("media/audio/clip.mp3")).thenReturn(new ImportAttachmentModel());

        var content = new ImportContentDto();
        content.setPayload(Map.of("fileRef", "media/audio/clip.mp3", "languageId", "lang-1"));

        var handler = new AudioUploadImportContentHandler();
        var vertex = handler.createContent(content, context);

        assertEquals("clip.mp3", vertex.getUrl());
        assertEquals("lang-1", vertex.getLanguage().getId());
        verify(context).requireDeclaredAttachment("media/audio/clip.mp3");
        verify(context).trackCreatedContentId(vertex.getId());
    }

    @Test
    void givenImageUploadContent_whenCreate_thenCreatesImageVertex() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.requireDeclaredAttachment("media/image/pic.png")).thenReturn(new ImportAttachmentModel());

        var content = new ImportContentDto();
        content.setPayload(Map.of("fileRef", "media/image/pic.png"));

        var handler = new ImageUploadImportContentHandler();
        var vertex = handler.createContent(content, context);

        assertEquals("pic.png", vertex.getUrl());
        verify(context).requireDeclaredAttachment("media/image/pic.png");
        verify(context).trackCreatedContentId(vertex.getId());
    }

    @Test
    void givenVideoUploadContent_whenCreate_thenCreatesVideoVertex() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.findLanguageById("lang-1")).thenReturn(language);
        when(context.requireDeclaredAttachment("media/video/clip.mp4")).thenReturn(new ImportAttachmentModel());

        var content = new ImportContentDto();
        content.setPayload(Map.of("fileRef", "media/video/clip.mp4", "languageId", "lang-1"));

        var handler = new VideoUploadImportContentHandler();
        var vertex = handler.createContent(content, context);

        assertEquals("clip.mp4", vertex.getUrl());
        assertEquals("lang-1", vertex.getLanguage().getId());
        verify(context).requireDeclaredAttachment("media/video/clip.mp4");
        verify(context).trackCreatedContentId(vertex.getId());
    }

    @Test
    void givenFlashcardRecordWithFrontAndBack_whenImportRecord_thenCreatesFlashcardAndAppliesConcepts() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var deck = FlashcardDeckVertex.create(traversalSource);
        deck.setId("deck-1");

        var front = TextContentVertex.create(traversalSource);
        front.setId("content-front");
        var back = TextContentVertex.create(traversalSource);
        back.setId("content-back");

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.getDeckVertex()).thenReturn(deck);
        when(context.resolveContentRef("content-front")).thenReturn(front);
        when(context.resolveContentRef("content-back")).thenReturn(back);

        var frontSlot = new ImportRecordSlotDto();
        frontSlot.setSlotKey("front");
        frontSlot.setContentRef("content-front");
        frontSlot.setConceptRefs(List.of("concept-front"));

        var backSlot = new ImportRecordSlotDto();
        backSlot.setSlotKey("back");
        backSlot.setContentRef("content-back");
        backSlot.setConceptRefs(List.of("concept-back"));

        var record = new ImportRecordDto();
        record.setRecordType("flashcard");
        record.setRecordId("record-1");
        record.setAttributes(Map.of("deckOrder", 7));
        record.setSlots(List.of(frontSlot, backSlot));

        var handler = new FlashcardImportRecordHandler();
        handler.importRecord(record, context);

        var flashcards = deck.getFlashcards();
        assertEquals(1, flashcards.size());
        assertEquals("record-1", flashcards.get(0).getId());
        assertEquals(7, flashcards.get(0).getDeckOrder());

        verify(context).applyConceptRef("concept-front");
        verify(context).applyConceptRef("concept-back");
        verify(context).trackCreatedFlashcardId("record-1");
    }

    @Test
    void givenFlashcardRecordMissingBackSlot_whenImportRecord_thenThrows() {
        var context = mock(ImportProcessingContext.class);

        var frontSlot = new ImportRecordSlotDto();
        frontSlot.setSlotKey("front");
        frontSlot.setContentRef("content-front");

        var record = new ImportRecordDto();
        record.setSlots(List.of(frontSlot));

        var handler = new FlashcardImportRecordHandler();

        assertThrows(IllegalArgumentException.class, () -> handler.importRecord(record, context));
    }

    @Test
    void givenPronunciationConcept_whenApply_thenCreatesPronunciation() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");

        var sourceText = TextContentVertex.create(traversalSource);
        sourceText.setId("text-1");
        sourceText.setLanguage(language);

        var audio = AudioContentVertex.create(traversalSource);
        audio.setId("audio-1");

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.resolveContentRef("source-text")).thenReturn(sourceText);
        when(context.resolveContentRef("source-audio")).thenReturn(audio);
        when(context.hasAudioLanguage(audio)).thenReturn(false);

        var concept = new ImportConceptDto();
        concept.setConceptId("pron-concept-1");
        concept.setPayload(Map.of(
                "sourceContentRef", "source-text",
                "pronunciationContentRef", "source-audio"));

        var handler = new PronunciationImportConceptHandler();
        handler.applyConcept(concept, context);

        var pronunciation = PronunciationVertex.findById(traversalSource, "pron-concept-1");
        assertNotNull(pronunciation);
        assertEquals("lang-1", audio.getLanguage().getId());
        verify(context).trackCreatedPronunciationId("pron-concept-1");
    }

    @Test
    void givenPronunciationConceptWithNonTextSource_whenApply_thenThrows() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

        var audio = AudioContentVertex.create(traversalSource);
        audio.setId("audio-1");

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.resolveContentRef("source-text")).thenReturn(audio);
        when(context.resolveContentRef("source-audio")).thenReturn(audio);

        var concept = new ImportConceptDto();
        concept.setPayload(Map.of(
                "sourceContentRef", "source-text",
                "pronunciationContentRef", "source-audio"));

        var handler = new PronunciationImportConceptHandler();

        assertThrows(IllegalArgumentException.class, () -> handler.applyConcept(concept, context));
    }

    @Test
    void givenTranscriptionConcept_whenApply_thenCreatesTranscriptionAndTracksIds() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();
        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");

        var sourceText = TextContentVertex.create(traversalSource);
        sourceText.setId("text-1");
        sourceText.setLanguage(language);

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.resolveContentRef("source-text")).thenReturn(sourceText);

        var concept = new ImportConceptDto();
        concept.setConceptId("trans-concept-1");
        concept.setPayload(Map.of(
                "sourceContentRef", "source-text",
                "transcription", "ni3 hao3",
                "transcriptionSystem", "pinyin"));

        var handler = new TranscriptionImportConceptHandler();
        handler.applyConcept(concept, context);

        var transcription = TranscriptionVertex.findById(traversalSource, "trans-concept-1");
        assertNotNull(transcription);
        assertEquals("pinyin", transcription.getSystem().getId());
        assertEquals("ni3 hao3", transcription.getTextContent().getValue());

        verify(context).trackCreatedTranscriptionId("trans-concept-1");
        verify(context).trackCreatedContentId(anyString());
    }

    @Test
    void givenTranscriptionConceptWithNonTextSource_whenApply_thenThrows() {
        GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

        var audio = AudioContentVertex.create(traversalSource);
        audio.setId("audio-1");

        var context = mock(ImportProcessingContext.class);
        when(context.getTraversalSource()).thenReturn(traversalSource);
        when(context.resolveContentRef("source-text")).thenReturn(audio);

        var concept = new ImportConceptDto();
        concept.setPayload(Map.of(
                "sourceContentRef", "source-text",
                "transcription", "value",
                "transcriptionSystem", "ipa"));

        var handler = new TranscriptionImportConceptHandler();

        assertThrows(IllegalArgumentException.class, () -> handler.applyConcept(concept, context));
    }
}

